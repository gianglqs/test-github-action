import React from 'react';
import { Line } from 'react-chartjs-2';

function LineChart({ chartName, chartData }) {
   return (
      <Line
         data={chartData}
         options={{
            scales: {
               y: {
                  beginAtZero: true,
                  title: {
                     text: 'Quantity',
                     display: true,
                  },
               },
               x: {
                  title: {
                     text: 'Year',
                     display: true,
                  },
               },
            },
            maintainAspectRatio: false,
            plugins: {
               title: {
                  display: true,
                  text: chartName,
               },
               legend: {
                  display: true,
               },
               tooltip: {
                  enabled: true,
               },
            },
         }}
      />
   );
}
export default LineChart;
