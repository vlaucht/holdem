import React from "react";
import ReactDOM from "react-dom/client";
import reportWebVitals from "./reportWebVitals";
import App from "./App";
import '@mantine/notifications/styles.css';

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement)


root.render(<App/>)

reportWebVitals()