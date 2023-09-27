import { makeStyles, DefaultTheme } from "@mui/styles"

export default makeStyles<DefaultTheme, any>((theme) => {
  return {
    appDialog__container: {},
    appDialog__title: {
      padding: theme.spacing(1, 2.5, 1, 2.5),
      borderBottom: "1px solid #e5e5e5",
      cursor: (props) => (props.draggable ? "move" : null),
      textTransform: "capitalize",
      display: "flex",
    },
    appDialog__title__icon: {
      fontSize: "20px !important",
    },
    appDialog__content: {
      height: (props) => props.height || null,
      margin: theme.spacing(1, 0),
      padding: theme.spacing(0, 2.5, 0, 2.5),
    },
    appDialog__closeIcon: {
      position: "absolute",
      right: "15px",
      top: "10px",
      fontSize: "20px !important",
      "&.disabled": {
        color: theme.palette.action.disabled,
        pointerEvents: "none",
      },
    },
    appDialog__buttonActions: {
      borderTop: "1px solid #e5e5e5",
      "& button": {
        textTransform: "uppercase",
      },
    },
    appDialog__loading: {
      position: "absolute",
      top: 0,
      left: 0,
      width: "100%",
    },
    appDialog__iconButton: {
      marginRight: "2px",
    },
  }
})
