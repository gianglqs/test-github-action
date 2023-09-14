import { Grid } from "@mui/material"

import { AppDialog } from "../AppDialog/AppDialog"
import FormControlledTextField from "@/components/FormController/TextField"
import { useForm } from "react-hook-form"
import { useEffect, useMemo, useState } from "react"
import FormControllerAutocomplete from "@/components/FormController/Autocomplete"
import dashboardApi from "@/api/dashboard.api"
import { useDispatch } from "react-redux"
import { commonStore, dashboardStore } from "@/store/reducers"
import { CreateUserFormValues } from "@/types/user"
import getValidationSchema from "./validationSchema"
import { yupResolver } from "@hookform/resolvers/yup"

const DialogUpdateUser: React.FC<any> = (props) => {
  const { open, onClose, detail } = props

  const dispatch = useDispatch()
  const [loading, setLoading] = useState(false)

  const validationSchema = useMemo(() => getValidationSchema(), [])
  const updateUserForm = useForm({
    resolver: yupResolver(validationSchema),
    shouldUnregister: false,
    defaultValues: detail,
  })

  const handleSubmitForm = updateUserForm.handleSubmit(
    async (data: CreateUserFormValues) => {
      const transformData = {
        userName: data.userName,
        email: data.email,
        password: data.password,
        role: {
          id: data.role,
        },
        defaultLocale: data.defaultLocale,
      }
      try {
        setLoading(true)
        await dashboardApi.createUser(transformData)
        const { data } = await dashboardApi.getUser({ search: "" })
        dispatch(dashboardStore.actions.setUserList(JSON.parse(data)?.userList))
        dispatch(
          commonStore.actions.setSuccessMessage("Create User Successfully")
        )
        onClose()
      } catch (error) {
        dispatch(commonStore.actions.setErrorMessage(error?.message))
      } finally {
        setLoading(false)
      }
    }
  )

  const roleOptions = useMemo(
    () => [
      { id: 1, roleName: "Admin" },
      { id: 2, roleName: "User" },
    ],
    []
  )

  const languageOptions = useMemo(
    () => [
      { id: "us", description: "English" },
      { id: "cn", description: "Chinese" },
    ],
    []
  )

  useEffect(() => {
    updateUserForm.reset(detail)
  }, [detail])

  return (
    <AppDialog
      open={open}
      loading={loading}
      //   onOk={handleSubmitForm}
      onClose={onClose}
      title="Create User"
      okText="Save"
    >
      <Grid container sx={{ paddingTop: 0.8, paddingBottom: 0.8 }} spacing={2}>
        <Grid item xs={12}>
          <FormControlledTextField
            control={updateUserForm.control}
            name="userName"
            label="Name"
            required
          />
        </Grid>
        <Grid item xs={12}>
          <FormControlledTextField
            control={updateUserForm.control}
            name="email"
            label="Email"
            required
          />
        </Grid>
        <Grid item xs={12}>
          <FormControlledTextField
            control={updateUserForm.control}
            type="password"
            name="password"
            label="Password"
            autoComplete="new-password"
            required
          />
        </Grid>
        <Grid item xs={6}>
          <FormControllerAutocomplete
            control={updateUserForm.control}
            name="role"
            label="User Role"
            renderOption={(prop, option) => `${option?.roleName}`}
            getOptionLabel={(option) => `${option?.roleName}`}
            required
            options={roleOptions}
          />
        </Grid>
        <Grid item xs={6}>
          <FormControllerAutocomplete
            control={updateUserForm.control}
            name="defaultLocale"
            label="Language"
            required
            options={languageOptions}
          />
        </Grid>
      </Grid>
    </AppDialog>
  )
}

export { DialogUpdateUser }
