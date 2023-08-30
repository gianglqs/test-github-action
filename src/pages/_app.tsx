import '@/theme/_global.css'
import { wrapper } from '@/store/config'
import type { AppProps } from 'next/app'
import { ThemeProvider } from '@mui/material/styles'
import appTheme from '@/theme/appTheme'


function MyApp({ Component, pageProps }: AppProps) {
  return (
    // <RollbarProvider >
    <ThemeProvider theme={appTheme}>
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
  );
}

export default wrapper.withRedux(MyApp)

