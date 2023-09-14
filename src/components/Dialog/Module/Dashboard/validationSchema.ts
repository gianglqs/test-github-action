import * as yup from "yup"
import _ from "lodash"

function getValidationSchema() {
  const schema = yup.object().shape({
    userName: yup.string().required("Name is required"),
    email: yup.string().required("Email is required"),
    password: yup.string().required("Password is required"),
    // role: yup.string().required("Role is required"),
    // defaultLocale: yup.string().required("Language is required"),
  })
  return schema
}

export default getValidationSchema
