import React from 'react';
import { Line } from 'react-chartjs-2';

function LineChart({ chartName, chartData, scales }) {
   return (
      <Line
         data={chartData}
         options={{
            scales,
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
