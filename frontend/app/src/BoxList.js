import React, { useEffect, useState } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';
import Converter from './Converter';
import AppNavbar from './AppNavbar';
import StockTable from './StockTable';
import { Link } from 'react-router-dom'
const  BoxList = () => {
    return (
        <div>
          <div id="currency-selection">
          <div>
          <Converter/>
          <StockTable/>
          </div>
      
        
      </div>
       </div>
        
       
    );
  }
  
  export default BoxList;