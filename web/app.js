const assemblyEditor = document.getElementById("assembly");
const statusMessage = document.getElementById("status");
const runButton = document.getElementById("run");
const ramGrid = document.getElementById("ram");

let runRequested = false;

async function requestState(path, body) {
  const options = { method: "POST" };
  if (body !== undefined) {
    options.headers = { "Content-Type": "text/plain; charset=utf-8" };
    options.body = body;
  }

  const response = await fetch(path, options);
  const data = await response.json();
  if (!response.ok) {
    throw new Error(data.error || `Request failed (${response.status})`);
  }
  return data;
}

function formatByte(value) {
  return Number(value).toString(16).toUpperCase().padStart(2, "0");
}

function renderState(state) {
  for (let register = 0; register < 4; register++) {
    const id = `R${register}`;
    document.getElementById(id).textContent = formatByte(state[id]);
  }

  document.getElementById("PC").textContent = formatByte(state.PC);
  document.getElementById("ZeroFlag").textContent = state.ZeroFlag ? "1" : "0";
  document.getElementById("cpu-state").textContent = state.running ? "Running" : "Halted";

  const fragment = document.createDocumentFragment();
  state.RAM.forEach((value, address) => {
    const cell = document.createElement("div");
    cell.className = "rounded-md border border-slate-800/80 bg-slate-950 px-1 py-2 text-center font-mono text-[11px] leading-4 sm:text-xs";
    cell.title = `RAM[${address}] = ${value}`;
    cell.innerHTML = `<span class="block text-[9px] text-slate-600">${formatByte(address)}</span><span class="text-slate-200"></span>`;
    cell.lastElementChild.textContent = formatByte(value);
    fragment.appendChild(cell);
  });
  ramGrid.replaceChildren(fragment);
}

function showError(error) {
  statusMessage.textContent = error.message;
  statusMessage.classList.remove("text-slate-400", "text-emerald-400");
  statusMessage.classList.add("text-rose-400");
}

function showStatus(message) {
  statusMessage.textContent = message;
  statusMessage.classList.remove("text-rose-400");
  statusMessage.classList.add("text-slate-400");
}

document.getElementById("assemble").addEventListener("click", async () => {
  runRequested = false;
  runButton.textContent = "Run";
  try {
    renderState(await requestState("/api/assemble", assemblyEditor.value));
    showStatus("Program assembled and loaded at address 0.");
  } catch (error) {
    showError(error);
  }
});

document.getElementById("step").addEventListener("click", async () => {
  if (runRequested) return;
  try {
    renderState(await requestState("/api/step"));
    showStatus("Executed one instruction.");
  } catch (error) {
    showError(error);
  }
});

runButton.addEventListener("click", async () => {
  if (runRequested) {
    runRequested = false;
    showStatus("Run paused.");
    return;
  }

  runRequested = true;
  runButton.textContent = "Stop";
  showStatus("Program running...");
  try {
    while (runRequested) {
      const state = await requestState("/api/step");
      renderState(state);
      if (!state.running) {
        showStatus("Program halted.");
        break;
      }
      await new Promise((resolve) => setTimeout(resolve, 40));
    }
  } catch (error) {
    showError(error);
  } finally {
    runRequested = false;
    runButton.textContent = "Run";
  }
});

document.getElementById("reset").addEventListener("click", async () => {
  runRequested = false;
  runButton.textContent = "Run";
  try {
    renderState(await requestState("/api/reset"));
    showStatus("CPU and RAM reset.");
  } catch (error) {
    showError(error);
  }
});

ramGrid.innerHTML = Array.from({ length: 256 }, (_, address) =>
  `<div class="rounded-md border border-slate-800/80 bg-slate-950 px-1 py-2 text-center font-mono text-[11px] leading-4 sm:text-xs"><span class="block text-[9px] text-slate-600">${formatByte(address)}</span><span class="text-slate-200">00</span></div>`
).join("");
