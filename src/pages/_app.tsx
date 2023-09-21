import "@/theme/_global.css"
import { wrapper } from "@/store/config"
import type { AppProps } from "next/app"
import appTheme from "@/theme/appTheme"
import { ThemeProvider } from "@mui/material/styles"
import { CssBaseline } from "@mui/material"
import AppMessagePopup from "@/components/App/MessagePopup"
import { useEffect } from "react"
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider"
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns"

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
      <LocalizationProvider dateAdapter={AdapterDateFns}>
        <Component {...pageProps} />
      </LocalizationProvider>
    </ThemeProvider>
  )
}

export default wrapper.withRedux(MyApp)
