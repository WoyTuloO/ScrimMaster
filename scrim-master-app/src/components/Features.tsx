import React from 'react';
import { Box, Container, Typography, Grid, Paper } from '@mui/material';

const featureData = [
    {
        title: 'Manage Schedules',
        description: 'Easily organize practice sessions, matches, and events.'
    },
    {
        title: 'Track Performance',
        description: 'Monitor player stats and team performance in real-time.'
    },
    {
        title: 'Instant Communication',
        description: 'Stay connected with your team through instant messaging.'
    },
    {
        title: 'Custom Strategies',
        description: 'Develop and customize your game strategies.'
    }
];

const FeaturesSection: React.FC = () => {
    return (
        <Box
            id="section2"
            sx={{
                opacity: 0,
                animation: 'fadeIn 1s forwards',
                '@keyframes fadeIn': {
                    from: { opacity: 0 },
                    to: { opacity: 1 }
                },

                height: '100vh',
                scrollSnapAlign: 'start',
                background: `linear-gradient(to right, rgba(0,0,0,0.1) 30%, rgba(205,115,0,1) 100%), url(/../../public/val_cs.jpg)`,

                backgroundSize: 'cover',
                backgroundPosition: 'center',
                backgroundRepeat: 'no-repeat'
            }}
        >
            <Container
                sx={{
                    height: '100%',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: { xs: 'center', md: 'flex-end' }
                }}
            >
                <Box
                    sx={{
                        width: { xs: '90%', md: '40%' },
                        backgroundColor: 'rgba(255, 255, 255, 0.9)',
                        p: 4,
                        borderRadius: 2
                    }}
                >
                    <Typography variant="h3" sx={{ mb: 4, fontFamily: 'Montserrat', fontWeight: 700 }}>
                        Features
                    </Typography>
                    <Grid container spacing={2}>
                        {featureData.map((feature, index) => (
                            <Grid>
                                <Paper elevation={5} sx={{ p: 2 }}>
                                    <Typography variant="h6" sx={{ fontFamily: 'Montserrat', fontWeight: 700, mb: 1 }}>
                                        {feature.title}
                                    </Typography>
                                    <Typography variant="body1" sx={{ fontFamily: 'Montserrat' }}>
                                        {feature.description}
                                    </Typography>
                                </Paper>
                            </Grid>
                        ))}
                    </Grid>
                </Box>
            </Container>
        </Box>
    );
};

export default FeaturesSection;
