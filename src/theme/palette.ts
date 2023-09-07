import { darken, Palette } from "@mui/material"

export const secondaryColor = {
  main: "#F4F7FC",
  light: "#DAE1EC",
  dark: "#7D90B2",
  contrastText: "#2E3B52",
}

const palette = {
  secondary: secondaryColor,
  standardDrawing: {
    revision_changed: {
      main: "#ff9999",
      dark: darken("#ff9999", 0.2),
    },
    synchronized: {
      main: "#fef9e7",
      dark: darken("#fef9e7", 0.2),
    },
    out_of_sync: {
      main: "#fad7a0",
      dark: darken("#fad7a0", 0.2),
    },
  },
} as Palette

export default palette
