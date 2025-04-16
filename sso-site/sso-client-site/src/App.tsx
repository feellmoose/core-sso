import { RouterProvider } from "react-router-dom";
import { router } from "./router";
import { SnackbarProvider } from "notistack";
import { ErrorBoundaryContainer } from "./components/base/ErrorBoundary";

function App() {
  return (
    <>
      <ErrorBoundaryContainer>
        <SnackbarProvider>
          <RouterProvider router={router} />
        </SnackbarProvider>
      </ErrorBoundaryContainer>
    </>
  );
}

export default App;
