(function () {
  "use strict";

  const editor = document.getElementById("assembly");
  const status = document.getElementById("status");
  const cpuStatus = document.getElementById("cpu-status");
  const ramGrid = document.getElementById("ram");

  function hex(value) {
    return Number(value).toString(16).toUpperCase().padStart(2, "0");
  }

  function render(state) {
    state.registers.forEach((value, index) => {
      document.getElementById(`r${index}`).textContent = hex(value);
    });
    document.getElementById("pc").textContent = hex(state.PC);
    document.getElementById("zero-flag").textContent = state.ZeroFlag ? "1" : "0";
    cpuStatus.textContent = state.running ? "Running" : "Halted";

    const cells = document.createDocumentFragment();
    state.RAM.forEach((value, address) => {
      const cell = document.createElement("div");
      cell.className = "rounded-md border border-slate-800/80 bg-slate-950 px-1 py-2 text-center font-mono text-[11px] leading-4 sm:text-xs";
      cell.title = `RAM[${address}] = ${value}`;
      const addressLabel = document.createElement("span");
      addressLabel.className = "block text-[9px] text-slate-600";
      addressLabel.textContent = hex(address);
      const valueLabel = document.createElement("span");
      valueLabel.className = "text-slate-200";
      valueLabel.textContent = hex(value);
      cell.append(addressLabel, valueLabel);
      cells.appendChild(cell);
    });
    ramGrid.replaceChildren(cells);
  }

  function reportError(error) {
    status.textContent = error.message;
    status.classList.remove("text-slate-400", "text-emerald-400");
    status.classList.add("text-rose-400");
  }

  function report(message) {
    status.textContent = message;
    status.classList.remove("text-rose-400");
    status.classList.add("text-slate-400");
  }

  function execute(action, successMessage) {
    try {
      render(action());
      report(successMessage);
    } catch (error) {
      reportError(error);
    }
  }

  document.getElementById("assemble").addEventListener("click", () => {
    execute(() => window.cpuEmulator.assemble(editor.value), "Program assembled and loaded at address 0.");
  });

  document.getElementById("step").addEventListener("click", () => {
    execute(() => window.cpuEmulator.step(), "Executed one instruction.");
  });

  document.getElementById("run").addEventListener("click", () => {
    execute(() => window.cpuEmulator.run(), "Program halted.");
  });

  document.getElementById("reset").addEventListener("click", () => {
    execute(() => window.cpuEmulator.reset(), "CPU and RAM reset.");
  });

  render(window.cpuEmulator.getState());
})();
