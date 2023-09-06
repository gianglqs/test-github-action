import { Grid } from "@mui/material";

import { AppDialog } from "../AppDialog/AppDialog";
import FormControlledTextField from "@/components/FormController/TextField";
import { useForm } from "react-hook-form";
import { useMemo } from "react";

const DialogCreateUser: React.FC<any> = (props) => {
  const { open, onClose } = props;

  const createForm = useForm<any>({
    shouldUnregister: false,
    defaultValues: {},
  });

  const handleSubmitForm = createForm.handleSubmit((data) => {
    console.log(data);
  });

  const roleOptions = useMemo(
    () => [
      { id: 0, description: "User" },
      { id: 1, description: "Admin" },
    ],
    []
  );

  return (
    <AppDialog open={open} onOk={handleSubmitForm} onClose={onClose}>
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <FormControlledTextField
            control={createForm.control}
            name="name"
            label="Name"
          />
        </Grid>
        <Grid item xs={12}>
          <FormControlledTextField
            control={createForm.control}
            name="email"
            label="Email"
          />
        </Grid>
        <Grid item xs={12}>
          <FormControlledTextField
            control={createForm.control}
            name="password"
            label="Password"
          />
        </Grid>
        <Grid item xs={6}>
          <FormControlledTextField
            control={createForm.control}
            name="user"
            label="User"
          />
        </Grid>
        <Grid item xs={6}>
          <FormControlledTextField
            control={createForm.control}
            name="language"
            label="Language"
          />
        </Grid>
      </Grid>
    </AppDialog>
  );
};

export { DialogCreateUser };
