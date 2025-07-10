import { createRoot } from "react-dom/client";
import App from "./App";

const mount = document.createElement("div");
document.body.prepend(mount);

createRoot(mount).render(<App />);
