import "./LinkPrompt.css";

const LinkPrompt = () =>{
    return (
           <div id="inline-prompt" style="display:none; position:absolute; z-index:10000;">
            <input type="text" id="inline-prompt-input" placeholder="Enter URL" />
            <button id="inline-prompt-ok">✔</button>
            <button id="inline-prompt-cancel">✖</button>
          </div>
    );
}

export default LinkPrompt;