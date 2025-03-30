import React from 'react';
import { Box, Typography, Container } from '@mui/material';

const LandingPage: React.FC = () => {
    return (
        <Box
            sx={{
                scrollSnapType: 'y mandatory',
                overflowY: 'scroll',
                height: '100vh'
            }}
        >
            {/* Pierwsza sekcja – pełnoekranowy blok tytulowy */}
            <Box
                id="section1"
                sx={{
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

            {/* Druga sekcja – przykładowa zawartość */}
            <Box
                id="section2"
                sx={{
                    height: '100vh',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    backgroundColor: 'background.default',
                    scrollSnapAlign: 'start'
                }}
            >
                <Container>
                    <Typography variant="h4" sx={{ mb: 2 }}>
                        Sekcja 2
                    </Typography>
                    <Typography variant="body1">
                        Tutaj możesz umieścić dodatkową zawartość, opisy, przyciski lub inne elementy.
                    </Typography>
                </Container>
            </Box>

            {/*/!* Kolejna sekcja *!/*/}
            {/*<Box*/}
            {/*    id="section3"*/}
            {/*    sx={{*/}
            {/*        height: '100vh',*/}
            {/*        display: 'flex',*/}
            {/*        alignItems: 'center',*/}
            {/*        justifyContent: 'center',*/}
            {/*        backgroundColor: '#e0e0e0',*/}
            {/*        scrollSnapAlign: 'start'*/}
            {/*    }}*/}
            {/*>*/}
            {/*    <Container>*/}
            {/*        <Typography variant="h4" sx={{ mb: 2 }}>*/}
            {/*            Sekcja 3*/}
            {/*        </Typography>*/}
            {/*        <Typography variant="body1">*/}
            {/*            Kolejna sekcja z zawartością.*/}
            {/*        </Typography>*/}
            {/*    </Container>*/}
            {/*</Box>*/}
        </Box>
    );
};

export default LandingPage;
