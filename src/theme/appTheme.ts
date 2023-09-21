import { createTheme } from "@mui/material"

import typography from "./typography"
import components from "./components"
import palette from "./palette"

const theme = createTheme({
  typography,
  components,
  shape: {
    borderRadius: 2,
  },
  palette,
})

export default theme
