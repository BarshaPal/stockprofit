import React from 'react';
import './App.css';
import AppNavbar from './AppNavbar';
import BoxList from './BoxList';
import { Link } from 'react-router-dom';
import { Button, Container } from 'reactstrap';

const Home = () => {
  return (
    <div>
      <AppNavbar/>
      <Container fluid>
        {/* <Button color="link"><Link to="/groups">Manage JUG Tour</Link></Button> */}
        <BoxList/>
      </Container>
    </div>
  );
}

export default Home;