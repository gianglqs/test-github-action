import Grid from "@mui/material/Grid"
import Paper from "@mui/material/Paper"
import { Autocomplete, Button, TextField } from "@mui/material"

import { DataTable, DataTablePagination } from "@/components"

import { AppLayout } from "@/components/App/Layout"
import { bookingStore, commonStore } from "@/store/reducers"
import FormControlledTextField from "@/components/FormController/TextField"
import { useForm } from "react-hook-form"
import FormControllerAutocomplete from "@/components/FormController/Autocomplete"
import { AppAutocomplete } from "@/components/App/Autocomplete"
import _ from "lodash"
import { useDispatch, useSelector } from "react-redux"
import { AppFooter } from "@/components/App/Footer"

export default function Booking() {
  const listBookingOrder = useSelector(bookingStore.selectBookingList)
  const initDataFilter = useSelector(bookingStore.selectInitDataFilter)

  const dispatch = useDispatch()

  const bookingOrderForm = useForm({})

  const columns = [
    {
      field: "orderNo",
      flex: 0.8,
      headerName: "Order #",
    },
    {
      field: "region",
      flex: 0.8,
      headerName: "Region",
    },
    {
      field: "ctryCode",
      flex: 0.8,
      headerName: "Country",
    },
    {
      field: "dealerName",
      flex: 0.8,
      headerName: "Deale Name",
      renderCell(params) {
        return <span>{params.row.billTo?.dealerDivison}</span>
      },
    },
    {
      field: "Plant",
      flex: 0.8,
      headerName: "Plant",
      renderCell(params) {
        return <span>{params.row.apacSerial?.plant}</span>
      },
    },
    {
      field: "truckClass",
      flex: 0.8,
      headerName: "Class",
      renderCell(params) {
        return <span>{params.row.apacSerial?.metaSeries?.clazz}</span>
      },
    },
    {
      field: "series",
      flex: 0.8,
      headerName: "Series",
      renderCell(params) {
        return <span>{params.row.apacSerial?.metaSeries?.series}</span>
      },
    },
    {
      field: "model",
      flex: 0.8,
      headerName: "Models",
      renderCell(params) {
        return <span>{params.row.apacSerial?.model}</span>
      },
    },
    {
      field: "qty",
      flex: 0.8,
      headerName: "Qty",
    },
    {
      field: "Total Cost",
      flex: 0.8,
      headerName: "Total Cost",
    },
    {
      field: "DN",
      flex: 0.8,
      headerName: "DN",
    },
    {
      field: "DN After Surcharge",
      flex: 0.8,
      headerName: "DN After Surcharge",
    },
    {
      field: "Margin $ After Surcharge",
      flex: 1,
      headerName: "Margin $ After Surcharge",
    },
    {
      field: "Margin % After Surcharge",
      flex: 1,
      headerName: "Margin % After Surcharge",
    },
  ]

  const arrTmp = [
    { value: "dealer" },
    { value: "delser2" },
    { value: "dealer3" },
    { value: "delser4" },
  ]
  const handleClick = bookingOrderForm.handleSubmit(async (data: any) => {
    console.log(data)
  })

  const handleChangePage = (pageNo: number) => {
    dispatch(commonStore.actions.setTableState({ pageNo }))
    dispatch(bookingStore.sagaGetList())
  }

  const handleChangePerPage = (perPage: number) => {
    dispatch(commonStore.actions.setTableState({ perPage }))
    handleChangePage(1)
  }

  const tableState = useSelector(commonStore.selectTableState)

  return (
    <>
      <AppLayout entity="booking">
        <Grid container spacing={1}>
          <Grid item xs={4}>
            <Grid item xs={12}>
              <FormControlledTextField
                control={bookingOrderForm.control}
                name="orderNo"
                label="Order #"
                placeholder="Search order by ID"
              />
            </Grid>
          </Grid>
          <Grid item xs={2}>
            <AppAutocomplete
              options={initDataFilter.regions}
              label="Region"
              sx={{ height: 25, zIndex: 10 }}
              // onChange={handleChangeMatStandards}
              // value={selectedMatStandards}
              limitTags={1}
              disableListWrap
              primaryKeyOption="value"
              multiple
              disableCloseOnSelect
              renderOption={(prop, option) => `${option.value}`}
              getOptionLabel={(option) => `${option.value}`}
            />
          </Grid>
          <Grid item xs={2} sx={{ zIndex: 10, height: 25 }}>
            <AppAutocomplete
              options={initDataFilter.plants}
              label="Plant"
              sx={{ height: 25, zIndex: 10 }}
              // onChange={handleChangeMatStandards}
              // value={selectedMatStandards}
              limitTags={1}
              disableListWrap
              primaryKeyOption="value"
              multiple
              disableCloseOnSelect
              renderOption={(prop, option) => `${option.value}`}
              getOptionLabel={(option) => `${option.value}`}
            />
          </Grid>
          <Grid item xs={2}>
            <AppAutocomplete
              options={initDataFilter.metaSeries}
              label="Metaseries"
              sx={{ height: 25, zIndex: 10 }}
              // onChange={handleChangeMatStandards}
              // value={selectedMatStandards}
              limitTags={1}
              disableListWrap
              primaryKeyOption="value"
              multiple
              disableCloseOnSelect
              renderOption={(prop, option) => `${option.value}`}
              getOptionLabel={(option) => `${option.value}`}
            />
          </Grid>
          <Grid item xs={2} sx={{ zIndex: 10, height: 25 }}>
            <AppAutocomplete
              options={initDataFilter.dealers}
              label="Dealer"
              sx={{ height: 25, zIndex: 10 }}
              // onChange={handleChangeMatStandards}
              // value={selectedMatStandards}
              limitTags={1}
              disableListWrap
              primaryKeyOption="value"
              multiple
              disableCloseOnSelect
              renderOption={(prop, option) => `${option.value}`}
              getOptionLabel={(option) => `${option.value}`}
            />
          </Grid>

          <Grid item xs={2}>
            <AppAutocomplete
              options={initDataFilter.classes}
              label="Class"
              sx={{ height: 25, zIndex: 10 }}
              // onChange={handleChangeMatStandards}
              // value={selectedMatStandards}
              limitTags={1}
              disableListWrap
              primaryKeyOption="value"
              multiple
              disableCloseOnSelect
              renderOption={(prop, option) => `${option.value}`}
              getOptionLabel={(option) => `${option.value}`}
            />
          </Grid>
          <Grid item xs={2}>
            <AppAutocomplete
              options={initDataFilter.models}
              label="Model"
              sx={{ height: 25, zIndex: 10 }}
              // onChange={handleChangeMatStandards}
              // value={selectedMatStandards}
              limitTags={1}
              disableListWrap
              primaryKeyOption="value"
              multiple
              disableCloseOnSelect
              renderOption={(prop, option) => `${option.value}`}
              getOptionLabel={(option) => `${option.value}`}
            />
          </Grid>
          <Grid item xs={2} sx={{ zIndex: 10, height: 25 }}>
            <AppAutocomplete
              options={initDataFilter.segments}
              label="Segment"
              sx={{ height: 25, zIndex: 10 }}
              // onChange={handleChangeMatStandards}
              // value={selectedMatStandards}
              limitTags={1}
              disableListWrap
              primaryKeyOption="value"
              multiple
              disableCloseOnSelect
              renderOption={(prop, option) => `${option.value}`}
              getOptionLabel={(option) => `${option.value}`}
            />
          </Grid>
          <Grid item xs={2}>
            <FormControllerAutocomplete
              control={bookingOrderForm.control}
              name="aopMargin"
              label="AOP Margin %"
              options={[]}
            />
          </Grid>
          <Grid item xs={2}>
            <FormControllerAutocomplete
              control={bookingOrderForm.control}
              name="margin"
              label="Margin %"
              options={[]}
            />
          </Grid>
          <Grid item xs={2}>
            <Autocomplete
              disablePortal
              id="combo-box-demo"
              options={[]}
              size="small"
              renderInput={(params) => (
                <TextField {...params} label="From Date" />
              )}
            />
          </Grid>
          <Grid item xs={2}>
            <Autocomplete
              disablePortal
              id="combo-box-demo"
              options={[]}
              size="small"
              renderInput={(params) => (
                <TextField {...params} label="To Date" />
              )}
            />
          </Grid>
          <Grid item xs={2}>
            <Button
              variant="contained"
              onClick={handleClick}
              sx={{ width: "100%", height: 24 }}
            >
              Filter
            </Button>
          </Grid>
        </Grid>

        <Paper elevation={1} sx={{ marginTop: 2 }}>
          <Grid container>
            <DataTable
              hideFooter
              disableColumnMenu
              tableHeight={740}
              rowHeight={45}
              rows={listBookingOrder}
              rowBuffer={35}
              rowThreshold={25}
              columns={columns}
              getRowId={(params) => params.orderNo}
            />
          </Grid>
          <DataTablePagination
            page={tableState.pageNo}
            perPage={tableState.perPage}
            totalItems={tableState.totalItems}
            onChangePage={handleChangePage}
            onChangePerPage={handleChangePerPage}
          />
          <AppFooter />
        </Paper>
      </AppLayout>
    </>
  )
}
