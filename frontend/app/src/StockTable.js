import { Button, Table, Input } from 'reactstrap';
import React, { useState } from 'react';
import './StockTable.css';
import axios from 'axios';

const StockTable = () => {
  const getCurrentDate = () => {
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    return yesterday.toISOString().split("T")[0]; 
  };

  const GOOGLE_SCRIPT_ID = "AKfycbw2oPyjXPKy_GjUzPIloYsOkGyhAhpEPmfzANGUuR8F16_mtNkPMW3eYxjE-j2QCASl";
  const API_URL = `https://script.google.com/macros/s/${GOOGLE_SCRIPT_ID}/exec`;

  const [rows, setRows] = useState([
    { 
      purchaseDate: "", 
      purchaseRate: "", 
      sellingDate: getCurrentDate(),  
      sellingQuantity: "", 
      sellingPrice: "", 
      profitLoss: "", 
      errors: {} 
    }
  ]);

  const [attemptedAdd, setAttemptedAdd] = useState(false);

  const addRow = () => {
    const lastRow = rows[rows.length - 1];

    if (!lastRow.purchaseDate || !lastRow.sellingDate || !lastRow.sellingQuantity) {
      setAttemptedAdd(true);
      const updatedRows = [...rows];
      updatedRows[rows.length - 1].errors = {
        purchaseDate: !lastRow.purchaseDate,
        sellingDate: !lastRow.sellingDate,
        sellingQuantity: !lastRow.sellingQuantity
      };
      setRows(updatedRows);
      return;
    }

    setRows([...rows, { 
      purchaseDate: "", 
      purchaseRate: "", 
      sellingDate: getCurrentDate(), 
      sellingQuantity: "", 
      sellingPrice: "", 
      profitLoss: "", 
      errors: {} 
    }]);

    setAttemptedAdd(false);
  };

  const handleInputQuantityChange = (index, field, value) => {
    const updatedRows = [...rows];
    updatedRows[index][field] = value;
  
    if (attemptedAdd) {
      updatedRows[index].errors[field] = value === "";
    }
  
    if (field === "sellingQuantity") {
      if (updatedRows[index].originalSellingPrice === undefined) {
        updatedRows[index].originalSellingPrice = updatedRows[index].sellingPrice || 0;
      }
  
      updatedRows[index].sellingPrice = updatedRows[index].originalSellingPrice * value;
    }
   if (updatedRows[index].purchaseRate && updatedRows[index].sellingPrice) {
    updatedRows[index].profitLoss =
      (updatedRows[index].sellingPrice - updatedRows[index].purchaseRate) *
      updatedRows[index].sellingQuantity;
  }




  if (updatedRows[index].purchaseRate && updatedRows[index].sellingPrice) {
    updatedRows[index].profitLoss =
      (updatedRows[index].sellingPrice - updatedRows[index].purchaseRate) *
      updatedRows[index].sellingQuantity;
  }
    setRows(updatedRows);
  };
  

  const handleInputChange = (index, field, value) => {
    const updatedRows = [...rows];
    updatedRows[index][field] = value;

    if (attemptedAdd) {
      updatedRows[index].errors[field] = value === "";
    }

    setRows(updatedRows);
    if(field==="purchaseDate" && updatedRows[index].purchaseDate ){
      fetchExchangeRate(updatedRows[index].purchaseDate, index,field);
    }
    if(field==="sellingDate" && updatedRows[index].sellingDate ){
      fetchExchangeRate(updatedRows[index].sellingDate, index,field);
    }
    
  };

  const fetchExchangeRate = async (Date, rowIndex,field) => {
    try {
      const response = await axios.get(`${API_URL}?date=${Date}`);

      if (response.data) {
        const updatedRows = [...rows];
        if(field==="purchaseDate" ){
        updatedRows[rowIndex].purchaseRate = response.data.Open;}
        if(field==="sellingDate" )
        {
          updatedRows[rowIndex].sellingPrice = response.data.Open;
         
          updatedRows[rowIndex].originalSellingPrice = response.data.Open;
        
        }
        setRows(updatedRows);
      }
    } catch (error) {
      console.error("Error fetching exchange rate:", error);
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between mb-3">
        <h2>Stock Transactions</h2>
        <Button color="success">Calculate Profitable Income</Button>
      </div>

      <Table bordered className="table">
        <thead className="thead-dark">
          <tr>
            {rows.length > 1 && <th></th>}
            <th>Purchase Date <span style={{ color: "red" }}>*</span></th>
            <th>Stock Price at Purchased Date</th>
            <th>Selling Date <span style={{ color: "red" }}>*</span></th>
            <th>Selling Quantity<span style={{ color: "red" }}>*</span></th>
            <th>Selling Price</th>
            <th>Estimate Profit/Loss</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {rows.map((row, index) => (
            <tr key={index}>
              {rows.length > 1 && (
                <td>
                  <Button color="danger" onClick={() => setRows(rows.filter((_, i) => i !== index))}>×</Button>
                </td>
              )}
              <td><Input type="date" value={row.purchaseDate} onChange={(e) => handleInputChange(index, "purchaseDate", e.target.value)} invalid={attemptedAdd && row.errors.purchaseDate} /></td>
              <td><Input type="number" value={row.purchaseRate} placeholder="Enter price" /></td>
              <td><Input type="date" value={row.sellingDate} onChange={(e) => handleInputChange(index, "sellingDate", e.target.value)} invalid={attemptedAdd && row.errors.sellingDate} /></td>
              <td><Input type="number" value={row.sellingQuantity} onChange={(e) => handleInputQuantityChange(index, "sellingQuantity", e.target.value)} placeholder="Enter quantity" /></td>
              <td><Input type="number" value={row.sellingPrice} placeholder="Enter price" /></td>
              <td>
  {row.profitLoss !== "" && (
    <span style={{ color: row.profitLoss < 0 ? "red" : "green", fontWeight: "bold" }}>
      {row.profitLoss}
    </span>
  )}
</td>

              <td>{index === rows.length - 1 && <Button color="primary" onClick={addRow}>Add More</Button>}</td>
            </tr>
          ))}
        </tbody>
      </Table>
    </div>
  );
};

export default StockTable;
