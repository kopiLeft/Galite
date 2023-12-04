import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';

// PDF makers
window.generatePDFWithId = function(pivotId) {
     // Get the PivotTable element using the provided ID
     const pivotTable = document.getElementById(pivotId);
     // Check if the element with the given ID exists
        if (pivotTable) {
            // Use html2canvas to capture the element as an image on a canvas
            html2canvas(pivotTable).then(canvas => {
                 //html2canvas : 21519 ms
                // Create a new jsPDF instance
                const doc = new jsPDF();
                // Get the canvas data as an image
                const imgData = canvas.toDataURL('image/png');
                // Add the captured image of the element to the PDF
                // Adjust the dimensions and positioning as needed
                //addImage(imageData, format, x, y, width, height, alias, compression, rotation)
                //x Coordinate (in units declared at inception of PDF document) against left edge of the page
                //y Coordinate (in units declared at inception of PDF document) against upper edge of the page
                doc.addImage(imgData, 'PNG', 10, 10, 170, 150);
                const uniqueNumber = Math.floor(Math.random() * 1000000); // Generate a random number
                const pdfFileName = `pivotTable_${uniqueNumber}.pdf`;
                // Save the PDF file
                doc.save(pdfFileName);
            })
        } else {
            console.error(`Element with ID ${pivotId} not found.`);
        }
}

//Image makers
window.generateImageWithId = function(pivotId , type) {
    const pivotTable = document.getElementById(pivotId);

    if (pivotTable) {

        html2canvas(pivotTable).then(canvas => {
            const resizedCanvas = document.createElement('canvas');
            const resizedContext = resizedCanvas.getContext('2d');
            resizedCanvas.width = 1000; // Set the new canvas width
            resizedCanvas.height = 1000; // Set the new canvas height
            resizedContext.drawImage(canvas, 0, 0, 1000, 1000);
            resizedCanvas.toBlob(blob => {
                const link = document.createElement('a');
                const uniqueNumber = Math.floor(Math.random() * 1000000);
                const fileName = `pivotTableImage_${uniqueNumber}.${type}`;
                link.href = URL.createObjectURL(blob);
                link.download = fileName;

                link.click();
                URL.revokeObjectURL(link.href);
            }, 'image/' + type);
        });
    } else {
        console.error(`Element with ID ${pivotId} not found.`);
    }
}