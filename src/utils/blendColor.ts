function hexToRgb(hex) {
   var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
   return result
      ? {
           r: parseInt(result[1], 16),
           g: parseInt(result[2], 16),
           b: parseInt(result[3], 16),
        }
      : null;
}

export default function blendColor(listColor: any[]) {
   if (listColor.length == 0) return null;
   const listRGBColor = [];
   listColor.forEach((color) => {
      const rgbColor = hexToRgb(color);
      listRGBColor.push(rgbColor);
   });

   let totalR = 0;
   let totalG = 0;
   let totalB = 0;

   listRGBColor.forEach((color) => {
      totalR += color.r;
      totalG += color.g;
      totalB += color.b;
   });

   const averageR = Math.round(totalR / listRGBColor.length);
   const averageG = Math.round(totalG / listRGBColor.length);
   const averageB = Math.round(totalB / listRGBColor.length);

   return `rgb(${averageR},${averageG},${averageB})`;
}
