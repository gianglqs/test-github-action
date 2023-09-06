import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Paper,
} from "@mui/material";
import useStyles from "./styles";
import clsx from "clsx";

import { Unless, When } from "react-if";
import Draggable from "react-draggable";

function PaperComponent({ draggable, ...props }) {
  return (
    <>
      <When condition={draggable}>
        <Draggable
          handle=".MuiDialogTitle-root"
          cancel={'[class*="MuiDialogContent-root"]'}
          bounds="parent"
        >
          <Paper {...props} style={{ margin: 0 }} />
        </Draggable>
      </When>
      <Unless condition={draggable}>
        <Paper {...props} style={{ margin: 0 }} />
      </Unless>
    </>
  );
}

const AppDialog: React.FC<any> = (props) => {
  const {
    onOk,
    children,
    onClose,
    bodyStyles,
    id,
    okButtonProps,
    loading,
    ...dialogProps
  } = props;
  const classes = useStyles(props);
  return (
    <Dialog
      className={clsx(classes.appDialog__container)}
      onClose={onClose}
      PaperComponent={PaperComponent}
      {...dialogProps}
    >
      <DialogTitle className={classes.appDialog__title}>
        Create User
      </DialogTitle>
      <DialogContent
        className={classes.appDialog__content}
        style={{ ...bodyStyles }}
      >
        {children}
      </DialogContent>
      <DialogActions className={classes.appDialog__buttonActions}>
        <Button
          color="primary"
          autoFocus
          type="submit"
          form={id}
          disabled={loading}
          onClick={onOk}
          {...okButtonProps}
        >
          Save
        </Button>
        <Button color="primary" autoFocus disabled={loading} onClick={onClose}>
          Close
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export { AppDialog };
