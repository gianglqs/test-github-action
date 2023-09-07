import { Grid } from "@mui/material";

import { AppDialog } from "../AppDialog/AppDialog";
import FormControlledTextField from "@/components/FormController/TextField";
import { useForm } from "react-hook-form";
import { useMemo, useState } from "react";
import FormControllerAutocomplete from "@/components/FormController/Autocomplete";
import dashboardApi from "@/api/dashboard.api";
import { useDispatch } from "react-redux";
import { dashboardStore } from "@/store/reducers";

const DialogCreateUser: React.FC<any> = (props) => {
  const { open, onClose } = props;

  const dispatch = useDispatch();

  const [loading, setLoading] = useState(false);

  const createForm = useForm<any>({
    shouldUnregister: false,
    defaultValues: {},
  });

  const handleSubmitForm = createForm.handleSubmit(async (data: any) => {
    const tranformData = {
      userName: data.userName,
      email: data.email,
      password: data.password,
      role: {
        id: data.role,
      },
      defaultLocale: data.defaultLocale,
    };
    try {
      setLoading(true);
      await dashboardApi.createUser(tranformData);
      const { data } = await dashboardApi.getUser();
      dispatch(dashboardStore.actions.setUserList(JSON.parse(data)));
      onClose();
    } catch (error) {
      alert(error.message);
    } finally {
      setLoading(false);
    }
  });

  const roleOptions = useMemo(
    () => [
      { id: 1, description: "Admin" },
      { id: 2, description: "User" },
    ],
    []
  );

  const languageOptions = useMemo(
    () => [
      { id: "us", description: "English" },
      { id: "cn", description: "Chinese" },
    ],
    []
  );

  return (
    <AppDialog
      open={open}
      loading={loading}
      onOk={handleSubmitForm}
      onClose={onClose}
    >
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <FormControlledTextField
            control={createForm.control}
            name="userName"
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
            type="password"
          />
        </Grid>
        <Grid item xs={6}>
          <FormControllerAutocomplete
            control={createForm.control}
            name="role"
            label="User Role"
            options={roleOptions}
          />
        </Grid>
        <Grid item xs={6}>
          <FormControllerAutocomplete
            control={createForm.control}
            name="defaultLocale"
            label="Language"
            options={languageOptions}
          />
        </Grid>
      </Grid>
    </AppDialog>
  );
};

export { DialogCreateUser };
