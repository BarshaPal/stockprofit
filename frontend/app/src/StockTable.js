import { Button, Table, Input, FormFeedback } from 'reactstrap';
import React, { useState } from 'react';
import './StockTable.css';

const StockTable = () => {
  const [rows, setRows] = useState([
    { purchaseDate: "", purchaseRate: "", sellingDate: "", sellingQuantity: "", sellingPrice: "", profitLoss: "", errors: {} }
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

    setRows([...rows, { purchaseDate: "", purchaseRate: "", sellingDate: "", sellingQuantity: "", sellingPrice: "", profitLoss: "", errors: {} }]);
    setAttemptedAdd(false); 
  };

  const handleInputChange = (index, field, value) => {
    const updatedRows = [...rows];
    updatedRows[index][field] = value;

    if (attemptedAdd) {
      updatedRows[index].errors[field] = value === "";
    }

    setRows(updatedRows);
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
                  <Button color="danger" onClick={() => setRows(rows.filter((_, i) => i !== index))}>
                    ×
                  </Button>
                </td>
              )}
              <td>
                <Input 
                  type="date" 
                  value={row.purchaseDate} 
                  onChange={(e) => handleInputChange(index, "purchaseDate", e.target.value)} 
                  invalid={attemptedAdd && row.errors.purchaseDate} 
                />
                {attemptedAdd && row.errors.purchaseDate && <FormFeedback>Purchase Date is required.</FormFeedback>}
              </td>
              <td>
                <Input 
                  type="number" 
                  value={row.purchaseRate} 
                  onChange={(e) => handleInputChange(index, "purchaseRate", e.target.value)} 
                  placeholder="Enter price" 
                />
              </td>
              <td>
                <Input 
                  type="date" 
                  value={row.sellingDate} 
                  onChange={(e) => handleInputChange(index, "sellingDate", e.target.value)} 
                  invalid={attemptedAdd && row.errors.sellingDate} 
                />
                {attemptedAdd && row.errors.sellingDate && <FormFeedback>Selling Date is required.</FormFeedback>}
              </td>
              <td><Input type="number" value={row.sellingQuantity} onChange={(e) => handleInputChange(index, "sellingQuantity", e.target.value)}
              invalid={attemptedAdd && row.errors.sellingQuantity} placeholder="Enter quantity" />
                              {attemptedAdd && row.errors.sellingQuantity && <FormFeedback>Selling Quantity is required.</FormFeedback>}
                              </td>
              <td><Input type="number" value={row.sellingPrice} onChange={(e) => handleInputChange(index, "sellingPrice", e.target.value)} placeholder="Enter price" /></td>
              <td><Input type="number" value={row.profitLoss} disabled placeholder="Auto-calculated" /></td>
              <td>
              {index === rows.length - 1 && (
                  <Button color="primary" onClick={addRow}>
                    Add More
                  </Button>
                )}
                
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </div>
  );
};

export default StockTable;
