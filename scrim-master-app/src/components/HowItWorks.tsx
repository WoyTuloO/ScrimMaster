import React from 'react';
import { Box, Container, Paper, Typography } from '@mui/material';

const HowItWorksSection: React.FC = () => {
    return (
        <Box
            id="section3"
            sx={{
                opacity: 0,
                animation: 'fadeIn 1s forwards',
                '@keyframes fadeIn': {
                    from: { opacity: 0 },
                    to: { opacity: 1 }
                },
                height: '100vh',
                scrollSnapAlign: 'start',
                background: `linear-gradient(to right, rgba(0,0,0,0.2) 20%, rgba(185,95,0,0.2) 100%),url(/../../public/cshd2.jpeg)`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                backgroundRepeat: 'no-repeat',
                imageRendering: 'auto',
                position: 'relative'
            }}
        >
            <Container
                sx={{
                    height: '100%',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                }}
            >
                <Paper
                    elevation={4}
                    sx={{
                        p: 4,
                        backgroundColor: 'rgba(255,255,255,0.9)',
                        maxWidth: { xs: '90%', md: '50%' }
                    }}
                >
                    <Typography variant="h4" sx={{ fontFamily: 'Montserrat', fontWeight: 700, mb: 2 }}>
                        How It Works
                    </Typography>
                    <Typography variant="body1" sx={{ fontFamily: 'Montserrat' }}>
                        Our platform streamlines your team's workflow in just a few simple steps.
                        <br /><br />
                        1. Sign up and create your team.<br />
                        2. Schedule your training sessions and matches.<br />
                        3. Join a server and play a game.<br />
                        4. Track performance along the way.
                    </Typography>
                </Paper>
            </Container>
        </Box>
    );
};

export default HowItWorksSection;
