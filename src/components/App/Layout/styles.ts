import { makeStyles } from "@mui/styles"

export default makeStyles((theme) => {
  return {
    header__container: {
      display: "flex",
      alignItems: "center",
      justifyContent: "space-between",
      height: 60,
      flexDirection: "row !important" as any,
      backgroundColor: `${theme.palette.common.white} !important` as any,
      boxShadow: "none !important",
      color: theme.palette.common.black,
      padding: theme.spacing(0, 1.5),
    },
    navigation: {
      display: "flex",
      height: "100%",
      alignItems: "center",
      justifyContent: "space-between",
      marginRight: 10,
    },
    appLayout__container: {
      margin: theme.spacing(0, 1.5),
      // height: 910,
    },
    label: {
      letterSpacing: 0.5,
      marginRight: 20,
      fontWeight: 600,
      textTransform: "capitalize",
      opacity: 0.7,
      cursor: "pointer",
    },
    footer: {
      zIndex: 100,
    },
  }
})
