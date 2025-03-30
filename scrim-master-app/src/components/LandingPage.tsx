import React from 'react';
import { Box, Typography, Container, Button } from '@mui/material';
import Features from './Features.tsx';
import HowItWorks from "./HowItWorks";
import CTASection from "./CTASection";

const LandingPage: React.FC = () => {
    return (
        <Box
            sx={{
                scrollSnapType: 'y mandatory',
                overflowY: 'scroll',
                height: '100vh'
            }}
        >
            <Box
                id="section1"
                sx={{

                    opacity: 0,
                    animation: 'fadeIn 1s forwards',
                    '@keyframes fadeIn': {
                        from: { opacity: 0 },
                        to: { opacity: 1 }
                    },
                    height: '100vh',
                    background: `linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)),url(/../../public/img1.jpg)`,
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                    backgroundBlendMode: "darken",
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    scrollSnapAlign: 'start'
                }}
            >
                <Typography
                    variant="h1"
                    sx={{

                        color: 'white',
                        textAlign: 'center',
                        fontWeight: 'bold',
                        fontFamily: 'Montserrat',
                        px: 1,
                        borderRadius: 2,
                        p: 1,

                        fontSize: {xs: 40, md: 90},
                    }}
                >
                    Step Up <span style={{
                            background: 'linear-gradient(45deg, #FE6B8B, #FF8E53)',
                            WebkitBackgroundClip: 'text',
                            WebkitTextFillColor: 'transparent',}}>Your </span>
                    Teamplay
                </Typography>
            </Box>
            <Features />
            <HowItWorks />
            <CTASection />
        </Box>
    );
};

export default LandingPage;
