import * as yup from "yup"
import _ from "lodash"

function getValidationSchema() {
  const schema = yup.object().shape({
    userName: yup.string().required("Name is required"),
    email: yup
      .string()
      .required("Email is required")
      .email("Email format is not correct! Please check again."),
    password: yup.string().required("Password is required"),
  })
  return schema
}

export default getValidationSchema
