import "@/theme/_global.css"
import { wrapper } from "@/store/config"
import type { AppProps } from "next/app"
import appTheme from "@/theme/appTheme"
import { ThemeProvider } from "@mui/material/styles"
import { CssBaseline } from "@mui/material"

function MyApp({ Component, pageProps }: AppProps) {
  return (
    // <RollbarProvider >
    <ThemeProvider theme={appTheme}>
      <CssBaseline />
      <Component {...pageProps} />
    </ThemeProvider>
    /* <ProviderSSEDialog>
                <Unless condition={isLoginPage || isMaintenancePage}>
                  <AppHeader />
                </Unless>

                <Component {...pageProps} />
              </ProviderSSEDialog>
            </ErrorBoundary> */
    // </RollbarProvider>
  )
}

export default wrapper.withRedux(MyApp)
