import React from 'react';
import { Box, Container, Typography, Button } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

const CTASection: React.FC = () => {
    return (
        <Box
            id="cta"
            sx={{
                height: '100vh',
                scrollSnapAlign: 'start',
                background: `linear-gradient(to right, rgba(0,0,0,0.6), rgba(255,165,0,0.7)), url(/../../public/ctaimg.png)`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
            }}
        >
            <Container sx={{ textAlign: 'center', color: 'white' }}>
                <Typography variant="h3" sx={{ fontFamily: 'Montserrat', fontWeight: 700, mb: 3 }}>
                    Ready to Join?
                </Typography>
                <Typography variant="h6" sx={{ fontFamily: 'Montserrat', mb: 4 }}>
                    Elevate your teamplay. Take the leap and experience a new level of collaboration.
                </Typography>
                <Button
                    component={RouterLink}
                    to="/register"
                    variant="contained"
                    color="secondary"
                    size="large"
                    sx={{ fontFamily: 'Montserrat', fontWeight: 700 }}
                >
                    Get Started
                </Button>
            </Container>
        </Box>
    );
};

export default CTASection;
