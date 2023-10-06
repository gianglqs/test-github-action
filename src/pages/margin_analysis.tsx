import {
  AppAutocomplete,
  AppDateField,
  AppLayout,
  AppSearchBar,
  AppTextField,
  DataTable,
} from "@/components"

import _ from "lodash"

import Grid from "@mui/material/Grid"
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Button,
  FormControlLabel,
  Paper,
  Radio,
  RadioGroup,
  Typography,
} from "@mui/material"
import { GridExpandMoreIcon } from "@mui/x-data-grid-pro"
import { useState } from "react"
import marginAnalysisApi from "@/api/marginAnalysis.api"
import { useDispatch } from "react-redux"
import { commonStore, marginAnalysisStore } from "@/store/reducers"
import { DatePicker } from "@mui/x-date-pickers"
import { format as formatDate } from "date-fns"
import { useSelector } from "react-redux"

export default function MarginAnalysis() {
  const dispatch = useDispatch()
  const [year, setYear] = useState("")
  const handleChangeDate = (date) => {
    setYear(formatDate(date, "yyyy-MM-dd"))
  }

  const dealerList = useSelector(marginAnalysisStore.selectDealerList)

  const [valueCurrency, setValueCurrency] = useState("USD")
  const handleChange = (event) => {
    setValueCurrency(event.target.value)
  }

  const [valueSearch, setValueSearch] = useState({ value: "", error: false })
  const handleSearch = (value) => {
    setValueSearch({ value: value, error: false })
  }

  const [listDataAnalysis, setListDataAnalysis] = useState([])
  const [marginAnalysisSummary, setMarginAnalysisSummary] = useState(null)
  const [openAccordion, setOpenAccordion] = useState(true)
  const [openAccordionTable, setOpenAccordionTable] = useState(true)
  const [dealer, setDealer] = useState("")

  const handleFilterMarginAnalysis = async () => {
    try {
      if (valueSearch.value === "") {
        setValueSearch({ value: "", error: true })
        return
      }

      const transformData = {
        modelCode: valueSearch.value,
        currency: {
          currency: valueCurrency,
        },
        monthYear: year === "" ? "" : `${year.slice(0, 7)}-01`,
      }

      const { data } = await marginAnalysisApi.getListMarginAnalysis({
        ...transformData,
        dealer: dealer,
      })

      const analysisSummary =
        await marginAnalysisApi.getListMarginAnalysisSummary(transformData)

      setMarginAnalysisSummary(analysisSummary?.data)

      setListDataAnalysis(data?.MarginAnalystData)
      setOpenAccordion(true)
      setOpenAccordionTable(true)
    } catch (error) {
      dispatch(commonStore.actions.setErrorMessage("Model code does not exist"))
    }
  }

  const handleSelectDealer = (e, option) => {
    setDealer(_.isNil(option) ? "" : option?.value)
  }

  const columns = [
    {
      field: "optionCode",
      flex: 0.8,
      headerName: "Part #",
    },
    {
      field: "listPrice",
      flex: 0.8,
      headerName: "List Price",
    },
    {
      field: "costRMB",
      flex: 0.8,
      headerName: "Cost RMB",
    },
    {
      field: "dealerNet",
      flex: 0.4,
      headerName: "DN",
    },
    {
      field: "dealer",
      flex: 0.4,
      headerName: "Dealer",
    },
    {
      field: "margin_aop",
      flex: 0.4,
      headerName: "Margin @ AOP",
    },
  ]

  return (
    <AppLayout entity="margin_analysis">
      <Grid container spacing={1.1} display="flex" alignItems="center">
        <Grid item xs={2}>
          {/* <AppSearchBar onSearch={handleSearch}></AppSearchBar>
           */}
          <AppTextField
            label="Model Code #"
            onChange={(e) => handleSearch(e.target.value)}
            value={valueSearch.value}
            error={valueSearch.error}
            helperText="Model Code # is Required"
            required
          />
        </Grid>
        <Grid item xs={2.5}>
          <AppAutocomplete
            options={dealerList}
            label="Dealer"
            sx={{ height: 25, zIndex: 10 }}
            onChange={handleSelectDealer}
            primaryKeyOption="value"
            disableClearable={false}
            renderOption={(prop, { value }) => `${value}`}
            getOptionLabel={({ value }) => `${value}`}
          />
        </Grid>
        <Grid item xs={1}>
          <RadioGroup
            row
            value={valueCurrency}
            onChange={handleChange}
            aria-labelledby="demo-row-radio-buttons-group-label"
            name="row-radio-buttons-group"
            sx={{
              display: "flex",
              justifyContent: "space-between",
              marginLeft: 1,
              paddingTop: 0,
            }}
          >
            <FormControlLabel value="USD" control={<Radio />} label="USD" />
            <FormControlLabel value="AUD" control={<Radio />} label="AUD" />
          </RadioGroup>
        </Grid>
        <Grid item xs={1.5}>
          <DatePicker
            onChange={handleChangeDate}
            label="Month and Year"
            views={["month", "year"]}
            sx={{ paddingLeft: -1 }}
          />
        </Grid>
        <Grid item xs={1}>
          <Button
            variant="contained"
            onClick={handleFilterMarginAnalysis}
            sx={{ width: "50%", height: 24 }}
          >
            Filter
          </Button>
        </Grid>
        <Grid item xs={12}>
          <Accordion
            expanded={openAccordion}
            onChange={(e, expanded) => setOpenAccordion(expanded)}
          >
            <AccordionSummary
              expandIcon={<GridExpandMoreIcon />}
              aria-controls="panel1a-content"
              id="panel1a-header"
            >
              <Typography></Typography>
            </AccordionSummary>
            <AccordionDetails>
              <Grid container spacing={1}>
                <Grid item xs={4}>
                  <Paper elevation={3} sx={{ padding: 2, height: 220 }}>
                    <div className="space-between-element">
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        Margin Analysis @ AOP Rate
                      </Typography>
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .marginAopRate
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Cost Uplift
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .costUplift
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Manufacturing Cost (RMB)
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .manufacturingCostRMB
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Add: Warranty
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .addWarranty
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Surcharge (inland,handling etc)
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .surcharge
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Duty (AU BT Only)
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .duty
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Freight (Au Only)
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .freight
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Li-lon B or C included
                      </Typography>
                      <Typography variant="body1" component="span">
                        {_.isNil(
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .liIonIncluded
                        )
                          ? ""
                          : marginAnalysisSummary?.MarginAnalystSummaryAnnually
                              .liIonIncluded
                          ? "Yes"
                          : "No"}
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Total Cost RMB
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .totalCostRMB
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        Full Cost USD @AOP Rate
                      </Typography>
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .fullCostAopRate
                        }
                      </Typography>
                    </div>
                  </Paper>
                </Grid>
                <Grid item xs={4}>
                  <Paper elevation={3} sx={{ padding: 2, height: 220 }}>
                    <div className="space-between-element">
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        Margin Analysis @ Mthly I/L Rate
                      </Typography>
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .marginAopRate
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Cost Uplift
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .costUplift
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Manufacturing Cost (RMB)
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .manufacturingCostRMB
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Add: Warranty
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .addWarranty
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Surcharge (inland,handling etc)
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .surcharge
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Duty (AU BT Only)
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .duty
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Freight (Au Only)
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .freight
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Li-lon B or C included
                      </Typography>
                      <Typography variant="body1" component="span">
                        {_.isNil(
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .liIonIncluded
                        )
                          ? ""
                          : marginAnalysisSummary?.MarginAnalystSummaryMonthly
                              .liIonIncluded
                          ? "Yes"
                          : "No"}
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Total Cost RMB
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .totalCostRMB
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        Full Cost USD @AOP Rate
                      </Typography>
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .fullCostAopRate
                        }
                      </Typography>
                    </div>
                  </Paper>
                </Grid>
                <Grid item xs={4}>
                  <Paper elevation={3} sx={{ padding: 2, height: 90 }}>
                    <div className="space-between-element">
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        AOP 2022
                      </Typography>
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        0
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        Target Margin % ={">"}
                      </Typography>
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        0
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        Margin guideline % ={">"}
                      </Typography>
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        0
                      </Typography>
                    </div>
                  </Paper>
                </Grid>
                <Grid item xs={4}>
                  <Paper elevation={3} sx={{ padding: 2, height: 120 }}>
                    <div className="space-between-element">
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        Total List Price USD
                      </Typography>
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .totalListPrice
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Blended Discount %
                      </Typography>
                      <Typography variant="body1" component="span">
                        {_.isNil(
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .blendedDiscountPercentage
                        )
                          ? ""
                          : `${(
                              marginAnalysisSummary
                                ?.MarginAnalystSummaryAnnually
                                .blendedDiscountPercentage * 100
                            ).toFixed(2)}%`}
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        DN USD
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .dealerNet
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Margin $
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .margin
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Margin % @ AOP rate
                      </Typography>
                      <Typography variant="body1" component="span">
                        {_.isNil(
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .marginPercentAopRate
                        )
                          ? ""
                          : `${(
                              marginAnalysisSummary
                                ?.MarginAnalystSummaryAnnually
                                .marginPercentAopRate * 100
                            ).toFixed(2)}%`}
                      </Typography>
                    </div>
                  </Paper>
                </Grid>
                <Grid item xs={4}>
                  <Paper elevation={3} sx={{ padding: 2, height: 120 }}>
                    <div className="space-between-element">
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        Total List Price USD
                      </Typography>
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .totalListPrice
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Blended Discount %
                      </Typography>
                      <Typography variant="body1" component="span">
                        {_.isNil(
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .blendedDiscountPercentage
                        )
                          ? ""
                          : `${(
                              marginAnalysisSummary?.MarginAnalystSummaryMonthly
                                .blendedDiscountPercentage * 100
                            ).toFixed(2)}%`}
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        DN USD
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .dealerNet
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Margin $
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .margin
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Margin % @ AOP rate
                      </Typography>
                      <Typography variant="body1" component="span">
                        {_.isNil(
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .marginPercentAopRate
                        )
                          ? ""
                          : `${(
                              marginAnalysisSummary?.MarginAnalystSummaryMonthly
                                .marginPercentAopRate * 100
                            ).toFixed(2)}%`}
                      </Typography>
                    </div>
                  </Paper>
                </Grid>
                <Grid item xs={4}></Grid>
                <Grid item xs={4}>
                  <Paper elevation={3} sx={{ padding: 2, height: 120 }}>
                    <div className="space-between-element">
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        For US Pricing @ AOP rate
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Manufacturing Cost (USD)
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .manufacturingCost
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Warranty (USD)
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .warranty
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Surcharge (USD)
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .surcharge
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Total Cost (USD)
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .totalCost
                        }
                      </Typography>
                    </div>
                  </Paper>
                </Grid>
                <Grid item xs={4}>
                  <Paper elevation={3} sx={{ padding: 2, height: 120 }}>
                    <div className="space-between-element">
                      <Typography
                        sx={{ fontWeight: "bold" }}
                        variant="body1"
                        component="span"
                      >
                        For US Pricing @ AOP rate
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Manufacturing Cost (USD)
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .manufacturingCostAop
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Warranty (USD)
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .warranty
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Surcharge (USD)
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .surcharge
                        }
                      </Typography>
                    </div>
                    <div className="space-between-element">
                      <Typography variant="body1" component="span">
                        Total Cost (USD)
                      </Typography>
                      <Typography variant="body1" component="span">
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .totalCost
                        }
                      </Typography>
                    </div>
                  </Paper>
                </Grid>
              </Grid>
            </AccordionDetails>
          </Accordion>
          <Accordion
            expanded={openAccordionTable}
            onChange={(e, expanded) => setOpenAccordionTable(expanded)}
          >
            <AccordionSummary
              expandIcon={<GridExpandMoreIcon />}
              aria-controls="panel1a-content"
              id="panel1a-header"
            >
              <Typography></Typography>
            </AccordionSummary>
            <AccordionDetails>
              <DataTable
                hideFooter
                disableColumnMenu
                tableHeight={openAccordion ? 195 : 710}
                sx={{ margin: -2 }}
                rowHeight={50}
                rows={listDataAnalysis}
                columns={columns}
              />
            </AccordionDetails>
          </Accordion>
        </Grid>
      </Grid>
    </AppLayout>
  )
}
