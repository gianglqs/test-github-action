import "@/theme/_global.css"
import { wrapper } from "@/store/config"
import type { AppProps } from "next/app"
import appTheme from "@/theme/appTheme"
import { ThemeProvider } from "@mui/material/styles"
import { CssBaseline } from "@mui/material"
import AppMessagePopup from "@/components/App/MessagePopup"
import { useEffect } from "react"

function MyApp({ Component, pageProps }: AppProps) {
  useEffect(() => {
    const jssStyles = document.querySelector("#jss-server-side")
    if (jssStyles) {
      jssStyles.parentElement.removeChild(jssStyles)
    }
  }, [])

  return (
    <ThemeProvider theme={appTheme}>
      <CssBaseline />
      <AppMessagePopup />
      <Component {...pageProps} />
    </ThemeProvider>
  )
}

export default wrapper.withRedux(MyApp)
