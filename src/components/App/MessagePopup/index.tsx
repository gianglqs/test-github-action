import { forwardRef } from "react"
import { useSelector, useDispatch } from "react-redux"

import { Snackbar, Alert, AlertProps } from "@mui/material"

import { commonStore } from "@/store/reducers"

const AppAlert = forwardRef<HTMLDivElement, AlertProps>(function (props, ref) {
  return (
    <Alert
      elevation={6}
      ref={ref}
      variant="filled"
      {...props}
      sx={{ fontWeight: "bold" }}
    />
  )
})

const AppMessagePopup: React.FC = () => {
  const dispatch = useDispatch()

  const messageState = useSelector(commonStore.selectMessageState)

  const handleClose = () => {
    dispatch(commonStore.actions.setDisplayMessage(false))
  }

  return (
    <Snackbar
      open={messageState.display}
      message={messageState.message}
      autoHideDuration={7000}
      anchorOrigin={{
        vertical: "top",
        horizontal: messageState.status === "info" ? "center" : "right",
      }}
      onClose={handleClose}
    >
      <AppAlert onClose={handleClose} severity={messageState.status}>
        {messageState.message}
      </AppAlert>
    </Snackbar>
  )
}

export default AppMessagePopup
