import { Button, ButtonGroup, Container, Table } from 'reactstrap';
import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';
import React, { useEffect, useState } from 'react';
import {Input} from 'reactstrap';

const  Converter = () => {

const [sourceCurrency, setSourceCurrency] = useState("AED");
const [targetCurrency, setTargetCurrency] = useState("INR");
const [conversionRate, setConversionRate] = useState(17.39); 
return (
    <div className="container mt-4">
      <div className="text-center mb-4">
      <h2 className="mb-2" style={{ color: "#5A4A76" }}>CURRENCY CONVERTER</h2>
      <p>1 {sourceCurrency} equals</p>
      <h3>{conversionRate} {targetCurrency}</h3>
      <div className="d-flex justify-content-center align-items-center">
        <select className="form-control w-25 text-center mx-2" value={sourceCurrency} onChange={(e) => setSourceCurrency(e.target.value)}>
          <option value="AED">AED</option>
          <option value="USD">USD</option>
          <option value="EUR">EUR</option>
        </select>
        <h4 className="mx-2">→</h4>
        <select className="form-control w-25 text-center mx-2" value={targetCurrency} onChange={(e) => setTargetCurrency(e.target.value)}>
          <option value="INR">INR</option>
          <option value="USD">USD</option>
          <option value="GBP">GBP</option>
        </select>
      </div>
    </div>
    </div>
);
  };
  
  export default Converter;