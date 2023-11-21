import { AppLayout, AppTextField, DataTable } from '@/components';

import _ from 'lodash';

import Grid from '@mui/material/Grid';
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
} from '@mui/material';
import { GridExpandMoreIcon } from '@mui/x-data-grid-pro';
import { useCallback, useState } from 'react';
import marginAnalysisApi from '@/api/marginAnalysis.api';
import { useDispatch } from 'react-redux';
import { commonStore } from '@/store/reducers';
import { useDropzone } from 'react-dropzone';
import { parseCookies, setCookie } from 'nookies';
import axios from 'axios';

export default function MarginAnalysis() {
   const dispatch = useDispatch();

   const [valueCurrency, setValueCurrency] = useState('USD');
   const handleChange = (event) => {
      setValueCurrency(event.target.value);
   };

   const [valueSearch, setValueSearch] = useState({ value: '', error: false });
   const handleSearch = (value) => {
      setValueSearch({ value: value, error: false });
   };

   const [listDataAnalysis, setListDataAnalysis] = useState([]);
   const [marginAnalysisSummary, setMarginAnalysisSummary] = useState(null);
   const [openAccordion, setOpenAccordion] = useState(true);
   const [openAccordionTable, setOpenAccordionTable] = useState(true);
   const [uploadedFile, setUploadedFile] = useState({ name: '' });

   const [orderNumberValue, setOrderNumberValue] = useState({ value: '' });
   const handleOrderNumber = (value) => {
      setOrderNumberValue({ value: value });
   };

   const handleFilterMarginAnalysis = async () => {
      try {
         const cookies = parseCookies();

         if (valueSearch.value === '') {
            setValueSearch({ value: '', error: true });
            return;
         }

         const transformData = {
            modelCode: valueSearch.value,
            currency: valueCurrency,
            fileUUID: cookies['fileUUID'],
            orderNumber: orderNumberValue.value,
         };

         const { data } = await marginAnalysisApi.getEstimateMarginAnalystData({
            ...transformData,
         });

         const analysisSummary = data?.MarginAnalystSummary;
         const marginAnalystData = data?.MarginAnalystData;

         marginAnalystData.forEach((margin) => {
            margin.listPrice = margin.listPrice.toLocaleString();
            margin.manufacturingCost = margin.manufacturingCost.toLocaleString();
            margin.dealerNet = margin.dealerNet.toLocaleString();
            margin.margin_aop = margin.margin_aop.toLocaleString();
         });

         setMarginAnalysisSummary(analysisSummary);
         setListDataAnalysis(marginAnalystData);

         setOpenAccordion(true);
         setOpenAccordionTable(true);
      } catch (error) {
         dispatch(commonStore.actions.setErrorMessage(error.message));
      }
   };

   const handleUploadFile = async (file) => {
      let formData = new FormData();
      formData.append('file', file);

      let cookies = parseCookies();
      let token = cookies['token'];
      axios({
         method: 'post',
         url: `${process.env.NEXT_PUBLIC_BACKEND_URL}estimateMarginAnalystData`,
         data: formData,
         headers: {
            'Content-Type': 'multipart/form-data',
            Authorization: 'Bearer' + token,
         },
      })
         .then(function (response) {
            setCookie(null, 'fileUUID', response.data.fileUUID);
         })
         .catch(function (response) {
            console.log(response);
         });
   };

   const columns = [
      {
         field: 'optionCode',
         flex: 0.8,
         headerName: 'Part #',
      },
      {
         field: 'listPrice',
         flex: 0.8,
         headerName: 'List Price',
      },
      {
         field: 'manufacturingCost',
         flex: 0.8,
         headerName: 'Manufacturing Cost',
      },
      {
         field: 'dealerNet',
         flex: 0.4,
         headerName: 'DN',
      },
      {
         field: 'dealer',
         flex: 0.4,
         headerName: 'Dealer',
      },
      {
         field: 'margin_aop',
         flex: 0.4,
         headerName: 'Margin @ AOP',
      },
   ];

   return (
      <AppLayout entity="margin_analysis">
         <Grid container spacing={1.1} display="flex" alignItems="center">
            <Grid item xs={1}>
               <AppTextField
                  label="Model Code #"
                  onChange={(e) => handleSearch(e.target.value)}
                  value={valueSearch.value}
                  error={valueSearch.error}
                  helperText="Model Code # is required"
                  required
               />
            </Grid>
            <Grid item xs={1}>
               <AppTextField
                  label="Order Number #"
                  onChange={(e) => handleOrderNumber(e.target.value)}
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
                     display: 'flex',
                     justifyContent: 'space-between',
                     marginLeft: 1,
                     paddingTop: 0,
                  }}
               >
                  <FormControlLabel value="USD" control={<Radio />} label="USD" />
                  <FormControlLabel value="AUD" control={<Radio />} label="AUD" />
               </RadioGroup>
            </Grid>
            <Grid item xs={1}>
               <Button
                  variant="contained"
                  onClick={handleFilterMarginAnalysis}
                  sx={{ width: '100%', height: 24 }}
               >
                  Calculate
               </Button>
            </Grid>

            <Grid item xs={1}>
               <UploadFileDropZone
                  uploadedFile={uploadedFile}
                  setUploadedFile={setUploadedFile}
                  handleUploadFile={handleUploadFile}
               />
            </Grid>
            <Grid item xs={4}>
               <Typography fontSize={16}>File uploaded: {uploadedFile.name}</Typography>
            </Grid>

            <Grid item xs={12}>
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
                           <Paper elevation={2} sx={{ padding: 2, height: 220 }}>
                              <div className="space-between-element">
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    Margin Analysis @ AOP Rate
                                 </Typography>
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    {marginAnalysisSummary?.MarginAnalystSummaryAnnually.marginAopRate.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Cost Uplift
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {_.isNil(
                                       marginAnalysisSummary?.MarginAnalystSummaryAnnually
                                          .costUplift
                                    )
                                       ? ''
                                       : `${(
                                            marginAnalysisSummary?.MarginAnalystSummaryAnnually
                                               .costUplift * 100
                                         ).toFixed(2)}%`}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    {valueCurrency === 'USD'
                                       ? 'Manufacturing Cost (RMB) / (USD)'
                                       : 'Manufacturing Cost (USD)'}
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryAnnually.totalManufacturingCost.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Add: Warranty
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {_.isNil(
                                       marginAnalysisSummary?.MarginAnalystSummaryAnnually
                                          .addWarranty
                                    )
                                       ? ''
                                       : `${(
                                            marginAnalysisSummary?.MarginAnalystSummaryAnnually
                                               .addWarranty * 100
                                         ).toFixed(2)}%`}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Surcharge (inland,handling etc)
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {_.isNil(
                                       marginAnalysisSummary?.MarginAnalystSummaryAnnually.surcharge
                                    )
                                       ? ''
                                       : `${(
                                            marginAnalysisSummary?.MarginAnalystSummaryAnnually
                                               .surcharge * 100
                                         ).toFixed(2)}%`}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Duty (AU BT Only)
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {_.isNil(
                                       marginAnalysisSummary?.MarginAnalystSummaryAnnually.duty
                                    )
                                       ? ''
                                       : `${(
                                            marginAnalysisSummary?.MarginAnalystSummaryAnnually
                                               .duty * 100
                                         ).toFixed(2)}%`}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Freight (Au Only)
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryAnnually.freight}
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
                                       ? ''
                                       : marginAnalysisSummary?.MarginAnalystSummaryAnnually
                                            .liIonIncluded
                                       ? 'Yes'
                                       : 'No'}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    {valueCurrency === 'USD'
                                       ? 'Total Cost (RMB) / (USD)'
                                       : 'Total Cost (USD)'}
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryAnnually.totalCost.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    {valueCurrency === 'USD'
                                       ? 'Full Cost USD @AOP Rate'
                                       : 'Full Cost AUD @AOP Rate'}
                                 </Typography>
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    {marginAnalysisSummary?.MarginAnalystSummaryAnnually.fullCostAopRate.toLocaleString()}
                                 </Typography>
                              </div>
                           </Paper>
                        </Grid>
                        <Grid item xs={4}>
                           <Paper elevation={3} sx={{ padding: 2, height: 220 }}>
                              <div className="space-between-element">
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    Margin Analysis @ Mthly I/L Rate
                                 </Typography>
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    {marginAnalysisSummary?.MarginAnalystSummaryMonthly.marginAopRate.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Cost Uplift
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {_.isNil(
                                       marginAnalysisSummary?.MarginAnalystSummaryMonthly.costUplift
                                    )
                                       ? ''
                                       : `${(
                                            marginAnalysisSummary?.MarginAnalystSummaryMonthly
                                               .costUplift * 100
                                         ).toFixed(2)}%`}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    {valueCurrency === 'USD'
                                       ? 'Manufacturing Cost (RMB) / (USD)'
                                       : 'Manufacturing Cost (USD)'}
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryMonthly.totalManufacturingCost.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Add: Warranty
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {_.isNil(
                                       marginAnalysisSummary?.MarginAnalystSummaryMonthly
                                          .addWarranty
                                    )
                                       ? ''
                                       : `${(
                                            marginAnalysisSummary?.MarginAnalystSummaryMonthly
                                               .addWarranty * 100
                                         ).toFixed(2)}%`}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Surcharge (inland,handling etc)
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {_.isNil(
                                       marginAnalysisSummary?.MarginAnalystSummaryMonthly.surcharge
                                    )
                                       ? ''
                                       : `${(
                                            marginAnalysisSummary?.MarginAnalystSummaryMonthly
                                               .surcharge * 100
                                         ).toFixed(2)}%`}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Duty (AU BT Only)
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {_.isNil(
                                       marginAnalysisSummary?.MarginAnalystSummaryMonthly.duty
                                    )
                                       ? ''
                                       : `${(
                                            marginAnalysisSummary?.MarginAnalystSummaryMonthly
                                               .duty * 100
                                         ).toFixed(2)}%`}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Freight (Au Only)
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryMonthly.freight}
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
                                       ? ''
                                       : marginAnalysisSummary?.MarginAnalystSummaryMonthly
                                            .liIonIncluded
                                       ? 'Yes'
                                       : 'No'}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    {valueCurrency === 'USD'
                                       ? 'Total Cost (RMB) / (USD)'
                                       : 'Total Cost (USD)'}
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryMonthly.totalCost.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    {valueCurrency === 'USD'
                                       ? 'Full Cost USD @AOP Rate'
                                       : 'Full Cost AUD @AOP Rate'}
                                 </Typography>
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    {marginAnalysisSummary?.MarginAnalystSummaryMonthly.fullMonthlyRate.toLocaleString()}
                                 </Typography>
                              </div>
                           </Paper>
                        </Grid>
                        <Grid item xs={4}>
                           <Paper elevation={3} sx={{ padding: 2, height: 90 }}>
                              <div className="space-between-element">
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    AOP 2022
                                 </Typography>
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    0
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    Target Margin % ={'>'}
                                 </Typography>
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    0
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    Margin guideline % ={'>'}
                                 </Typography>
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
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
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    {valueCurrency === 'USD'
                                       ? 'Total List Price (USD)'
                                       : 'Total List Price (AUD)'}
                                 </Typography>
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    {marginAnalysisSummary?.MarginAnalystSummaryAnnually.totalListPrice.toLocaleString()}
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
                                       ? ''
                                       : `${(
                                            marginAnalysisSummary?.MarginAnalystSummaryAnnually
                                               .blendedDiscountPercentage * 100
                                         ).toFixed(2)}%`}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    {valueCurrency === 'USD' ? 'DN (USD)' : 'DN (AUD)'}
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryAnnually.dealerNet.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Margin $
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryAnnually.margin.toLocaleString()}
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
                                       ? ''
                                       : `${(
                                            marginAnalysisSummary?.MarginAnalystSummaryAnnually
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
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    {valueCurrency === 'USD'
                                       ? 'Total List Price (USD)'
                                       : 'Total List Price (AUD)'}
                                 </Typography>
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
                                    variant="body1"
                                    component="span"
                                 >
                                    {marginAnalysisSummary?.MarginAnalystSummaryMonthly.totalListPrice.toLocaleString()}
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
                                       ? ''
                                       : `${(
                                            marginAnalysisSummary?.MarginAnalystSummaryMonthly
                                               .blendedDiscountPercentage * 100
                                         ).toFixed(2)}%`}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    {valueCurrency === 'USD' ? 'DN (USD)' : 'DN (AUD)'}
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryMonthly.dealerNet.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Margin $
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryMonthly.margin.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Margin % @ AOP rate
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {_.isNil(
                                       marginAnalysisSummary?.MarginAnalystSummaryMonthly
                                          .marginPercentMonthlyRate
                                    )
                                       ? ''
                                       : `${(
                                            marginAnalysisSummary?.MarginAnalystSummaryMonthly
                                               .marginPercentMonthlyRate * 100
                                         ).toFixed(2)}%`}
                                 </Typography>
                              </div>
                           </Paper>
                        </Grid>
                        <Grid item xs={4}></Grid>
                        <Grid item xs={4}>
                           <Paper elevation={3} sx={{ padding: 2, height: 140 }}>
                              <div className="space-between-element">
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
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
                                    {marginAnalysisSummary?.MarginAnalystSummaryAnnually.manufacturingCostUSD.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Warranty (USD)
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryAnnually.warrantyCost.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Surcharge (USD)
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryAnnually.surchargeCost.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Total Cost Excluding Freight (USD)
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryAnnually.totalCostWithoutFreight.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Total Cost With Freight (USD)
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryAnnually.totalCostWithFreight.toLocaleString()}
                                 </Typography>
                              </div>
                           </Paper>
                        </Grid>
                        <Grid item xs={4}>
                           <Paper elevation={3} sx={{ padding: 2, height: 140 }}>
                              <div className="space-between-element">
                                 <Typography
                                    sx={{ fontWeight: 'bold' }}
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
                                    {marginAnalysisSummary?.MarginAnalystSummaryMonthly.manufacturingCostUSD.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Warranty (USD)
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryMonthly.warrantyCost.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Surcharge (USD)
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryMonthly.surchargeCost.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Total Cost Without Freight (USD)
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryMonthly.totalCostWithoutFreight.toLocaleString()}
                                 </Typography>
                              </div>
                              <div className="space-between-element">
                                 <Typography variant="body1" component="span">
                                    Total Cost With Freight (USD)
                                 </Typography>
                                 <Typography variant="body1" component="span">
                                    {marginAnalysisSummary?.MarginAnalystSummaryMonthly.totalCostWithFreight.toLocaleString()}
                                 </Typography>
                              </div>
                           </Paper>
                        </Grid>
                     </Grid>
                  </AccordionDetails>
               </Accordion>
            </Grid>
         </Grid>
      </AppLayout>
   );
}

function UploadFileDropZone(props) {
   const onDrop = useCallback((acceptedFiles) => {
      acceptedFiles.forEach((file) => {
         const reader = new FileReader();

         reader.onabort = () => console.log('file reading was aborted');
         reader.onerror = () => console.log('file reading has failed');
         reader.onload = () => {
            // Do whatever you want with the file contents
            const binaryStr = reader.result;
            console.log(binaryStr);
            props.setUploadedFile(file);
         };
         reader.readAsArrayBuffer(file);
         props.handleUploadFile(file);
      });
   }, []);

   const { getRootProps, getInputProps, open, fileRejections } = useDropzone({
      noClick: true,
      onDrop,
      maxSize: 1048576,
      maxFiles: 1,
      accept: {
         'excel/xlsx': ['.xlsx'],
      },
   });
   const dispatch = useDispatch();
   const isFileInvalid = fileRejections.length > 0 ? true : false;
   if (isFileInvalid) {
      const errors = fileRejections[0].errors;
      dispatch(
         commonStore.actions.setErrorMessage(
            `${errors[0].message} ${_.isNil(errors[1]) ? '' : `or ${errors[1].message}`}`
         )
      );
      console.log(fileRejections);
      fileRejections.splice(0, 1);
   }

   return (
      <div {...getRootProps()}>
         <input {...getInputProps()} />
         <Button
            type="button"
            onClick={open}
            variant="contained"
            sx={{ width: '100%', height: 24 }}
         >
            Select file
         </Button>
      </div>
   );
}