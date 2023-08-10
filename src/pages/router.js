import {createBrowserRouter} from "react-router-dom";
import LogIn from "./logIn";
import HomePage from "./homepage";

const routerManagement = createBrowserRouter([
    {
        path: "/",
        element: <LogIn/>
    },
    {
        path: "/homepage",
        element: <HomePage/>
    }
])
export default routerManagement;