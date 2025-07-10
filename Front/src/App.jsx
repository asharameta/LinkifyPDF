import { BrowserRouter, Routes, Route } from "react-router-dom";

import Header from "./components/Header/Header";
import Home from "./Pages/HomePage/HomePage";
import Library from "./Pages/LibraryPage/LibraryPage";
import Optimizer from "./Pages/OptimizerPage/OptimizerPage";


const App = () => (
  <BrowserRouter>
    <Header />
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/library" element={<Library />} />
      <Route path="/optimizer" element={<Optimizer />} />
    </Routes>
  </BrowserRouter>
);

export default App;