import Grid from "@mui/material/Grid"
import Paper from "@mui/material/Paper"
import { Autocomplete, Button, TextField } from "@mui/material"

import { DataTable } from "@/components"

import { useEffect, useState } from "react"
import { AppLayout } from "@/components/App/Layout"
import { useSelector } from "react-redux"
import { bookingStore } from "@/store/reducers"
import FormControlledTextField from "@/components/FormController/TextField"
import { useForm } from "react-hook-form"
import FormControllerAutocomplete from "@/components/FormController/Autocomplete"
import { AppAutocomplete } from "@/components/App/Autocomplete"
import _ from "lodash"

export default function Booking() {
  const listBookingOrder = useSelector(bookingStore.selectBookingList)
  const initDataFilter = useSelector(bookingStore.selectInitDataFilter)

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
  return (
    <AppLayout entity="booking">
      <Grid container spacing={1}>
        <Grid item xs={12}>
          <Grid item xs={9}>
            <FormControlledTextField
              control={bookingOrderForm.control}
              name="orderNo"
              label="Order #"
              placeholder="Search order by ID"
              sx={{ width: 1419 }}
            />
          </Grid>
        </Grid>
        <Grid item xs={1}>
          <FormControllerAutocomplete
            control={bookingOrderForm.control}
            name="region"
            label="Region"
            // options={initDataFilter.region}
            renderOption={(prop, option) => `${option}`}
            getOptionLabel={(option) => `${option}`}
          />
        </Grid>
        <Grid item xs={1}>
          <AppAutocomplete
            options={arrTmp}
            label="Dealer"
            // onChange={handleChangeMatStandards}
            // value={selectedMatStandards}
            limitTags={2}
            primaryKeyOption="value"
            multiple
            disableCloseOnSelect
            renderOption={(prop, option) => `${option.value}`}
            getOptionLabel={(option) => `${option.value}`}
          />
          {/* <FormControllerAutocomplete
            control={bookingOrderForm.control}
            name="dealer"
            multiple
            limitTags={3}
            // label="Dealer"
            options={initDataFilter.dealers}
            renderOption={(prop, option) => `${option}`}
            getOptionLabel={(option) => `${option}`}
          /> */}
        </Grid>
        <Grid item xs={1}>
          <FormControllerAutocomplete
            control={bookingOrderForm.control}
            name="plant"
            label="Plant"
            options={initDataFilter.plants}
            renderOption={(prop, option) => `${option}`}
            getOptionLabel={(option) => `${option}`}
          />
        </Grid>
        <Grid item xs={1}>
          <FormControllerAutocomplete
            control={bookingOrderForm.control}
            name="metaseries"
            label="Metaseries"
            options={initDataFilter.metaSeries}
            renderOption={(prop, option) => `${option}`}
            getOptionLabel={(option) => `${option}`}
          />
        </Grid>
        <Grid item xs={1}>
          <FormControllerAutocomplete
            control={bookingOrderForm.control}
            name="class"
            label="Class"
            options={initDataFilter.classes}
            renderOption={(prop, option) => `${option}`}
            getOptionLabel={(option) => `${option}`}
          />
        </Grid>
        <Grid item xs={1}>
          <FormControllerAutocomplete
            control={bookingOrderForm.control}
            name="model"
            label="Model"
            options={initDataFilter.models}
            renderOption={(prop, option) => `${option}`}
            getOptionLabel={(option) => `${option}`}
          />
        </Grid>
        <Grid item xs={1}>
          <FormControllerAutocomplete
            control={bookingOrderForm.control}
            name="segment"
            label="Segment"
            options={initDataFilter.segments}
            renderOption={(prop, option) => `${option}`}
            getOptionLabel={(option) => `${option}`}
          />
        </Grid>
        <Grid item xs={1}>
          <FormControllerAutocomplete
            control={bookingOrderForm.control}
            name="aopMargin"
            label="AOP Margin %"
            options={[]}
          />
        </Grid>
        <Grid item xs={4}>
          <FormControllerAutocomplete
            control={bookingOrderForm.control}
            name="margin"
            label="Margin %"
            sx={{ width: 150 }}
            options={[]}
          />
        </Grid>
        <Grid item xs={2}>
          <Autocomplete
            disablePortal
            id="combo-box-demo"
            options={[]}
            size="small"
            sx={{ width: 310 }}
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
            sx={{ width: 310 }}
            renderInput={(params) => <TextField {...params} label="To Date" />}
          />
        </Grid>
        <Grid item xs={2}>
          <Button variant="contained" sx={{ width: 310, height: 24 }}>
            Filter
          </Button>
        </Grid>
      </Grid>
      <Paper elevation={1} sx={{ marginTop: 2 }}>
        <Grid container>
          <DataTable
            hideFooter
            disableColumnMenu
            checkboxSelection
            tableHeight={800}
            rowHeight={45}
            rows={listBookingOrder}
            rowBuffer={35}
            rowThreshold={25}
            columns={columns}
            getRowId={(params) => params.orderNo}
          />
        </Grid>
      </Paper>
    </AppLayout>
  )
}
