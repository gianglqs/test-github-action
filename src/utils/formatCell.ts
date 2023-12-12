export const formatNumber = (num: any) => {
   if (typeof num === 'number' && num != Infinity && num != -Infinity && !isNaN(num)) {
      return num.toLocaleString(undefined, {
         minimumFractionDigits: 2,
         maximumFractionDigits: 2,
      });
   } else {
      return null;
   }
};
export const formatNumberPercentage = (num: any) => {
   if (typeof num === 'number' && num != Infinity && num != -Infinity && !isNaN(num)) {
      return (
         num.toLocaleString(undefined, {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
         }) + '%'
      );
   } else {
      return null;
   }
};

export const formatDate = (calendar: String) => {
   if (typeof calendar === 'string') return calendar.substring(0, 10);
};
