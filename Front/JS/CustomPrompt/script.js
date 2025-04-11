function customPrompt(message, defaultValue = "", x = window.innerWidth/2, y = window.innerHeight/2) {
    return new Promise((resolve) => {
      const box = document.getElementById("inline-prompt");
      const input = document.getElementById("inline-prompt-input");
      const okBtn = document.getElementById("inline-prompt-ok");
      const cancelBtn = document.getElementById("inline-prompt-cancel");
  
      input.value = defaultValue;
      box.style.left = `${x}px`;
      box.style.top = `${y}px`;
      box.style.display = "flex";
  
      input.focus();
  
      function cleanup() {
        box.style.display = "none";
        okBtn.removeEventListener("click", onOk);
        cancelBtn.removeEventListener("click", onCancel);
        input.removeEventListener("keydown", onKeydown);
      }
  
      function onOk() {
        cleanup();
        resolve(input.value);
      }
  
      function onCancel() {
        cleanup();
        resolve(null);
      }
  
      function onKeydown(e) {
        if (e.key === "Enter") onOk();
        if (e.key === "Escape") onCancel();
      }
  
      okBtn.addEventListener("click", onOk);
      cancelBtn.addEventListener("click", onCancel);
      input.addEventListener("keydown", onKeydown);
    });
  }
  