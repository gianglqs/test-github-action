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
import { commonStore } from "@/store/reducers"

export default function MarginAnalysis() {
  const dispatch = useDispatch()
  const [year, setYear] = useState("")
  const handleChangeDealer = (value) => {
    setYear(value)
  }

  const [valueCurrency, setValueCurrency] = useState("USD")
  const handleChange = (event) => {
    setValueCurrency(event.target.value)
  }

  const [valueSearch, setValueSearch] = useState("")
  const handleSearch = (value) => {
    setValueSearch(value)
  }

  const [listDataAnalysis, setListDataAnalysis] = useState([])
  const [marginAnalysisSummary, setMarginAnalysisSummary] = useState(null)

  console.log(listDataAnalysis)

  const handleFilterMarginAnalysis = async () => {
    try {
      const transformData = {
        modelCode: valueSearch,
        currency: {
          currency: valueCurrency,
        },
        monthYear: `${year.slice(0, 7)}-01`,
      }

      const { data } = await marginAnalysisApi.getListMarginAnalysis(
        transformData
      )

      const analysisSummary =
        await marginAnalysisApi.getListMarginAnalysisSummary(transformData)

      setMarginAnalysisSummary(analysisSummary?.data)

      setListDataAnalysis(data?.MarginAnalystData)
    } catch (error) {
      dispatch(commonStore.actions.setErrorMessage('Model code does not exist'))
    }
  }

  const columns = [
    {
      field: "partNumber",
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
      field: "dn",
      flex: 0.4,
      headerName: "DN",
    },
    {
      field: "dn",
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
      <Grid container spacing={1} display="flex" alignItems="center">
        <Grid item xs={2}>
          {/* <AppSearchBar onSearch={handleSearch}></AppSearchBar>
           */}
          <AppTextField
            label="Model Code#"
            onChange={(e) => handleSearch(e.target.value)}
            value={valueSearch}
          />
        </Grid>
        <Grid item xs={1}>
          <RadioGroup
            row
            value={valueCurrency}
            onChange={handleChange}
            aria-labelledby="demo-row-radio-buttons-group-label"
            name="row-radio-buttons-group"
          >
            <FormControlLabel value="USD" control={<Radio />} label="USD" />
            <FormControlLabel value="AUD" control={<Radio />} label="AUD" />
          </RadioGroup>
        </Grid>
        <Grid item xs={1.5}>
          <AppDateField
            label="Year"
            name="toDate"
            onChange={(e, value) => handleChangeDealer(value)}
            value={year}
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
          <Accordion>
            <AccordionSummary
              expandIcon={<GridExpandMoreIcon />}
              aria-controls="panel1a-content"
              id="panel1a-header"
            >
              <Typography>Accordion 1</Typography>
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
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryAnnually
                            .liIonIncluded
                        }
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
                        {
                          marginAnalysisSummary?.MarginAnalystSummaryMonthly
                            .liIonIncluded
                        }
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
                  <Paper elevation={3} sx={{ padding: 2, height: 220 }}></Paper>
                </Grid>
              </Grid>
            </AccordionDetails>
          </Accordion>
          <Accordion>
            <AccordionSummary
              expandIcon={<GridExpandMoreIcon />}
              aria-controls="panel1a-content"
              id="panel1a-header"
            >
              <Typography>Accordion 1</Typography>
            </AccordionSummary>
            <AccordionDetails>
              <DataTable
                hideFooter
                disableColumnMenu
                checkboxSelection
                tableHeight={450}
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
