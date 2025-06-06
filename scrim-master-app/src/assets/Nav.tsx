import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import Menu from '@mui/material/Menu';
import MenuIcon from '@mui/icons-material/Menu';
import Container from '@mui/material/Container';
import Button from '@mui/material/Button';
import Tooltip from '@mui/material/Tooltip';
import MenuItem from '@mui/material/MenuItem';
import AdbIcon from '@mui/icons-material/Adb';
import PersonIcon from '@mui/icons-material/Person';
import {Link as RouterLink, useNavigate} from 'react-router-dom';
import { AuthContext } from './AuthContext';
import GamepadIcon from '@mui/icons-material/Gamepad';

const pages = ['Teams', 'Scrims', 'Players'];
const settings = ['Profile', 'Account', 'My Teams', 'Dashboard', 'Logout'];

const ResponsiveAppBar: React.FC = () => {
    const { isAuthenticated, logout, user } = React.useContext(AuthContext);
    const navigate = useNavigate();

    const [anchorElNav, setAnchorElNav] = React.useState<null | HTMLElement>(null);
    const [anchorElUser, setAnchorElUser] = React.useState<null | HTMLElement>(null);

    const handleOpenNavMenu = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorElNav(event.currentTarget);
    };
    const handleOpenUserMenu = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorElUser(event.currentTarget);
    };

    const handleCloseNavMenu = () => {
        setAnchorElNav(null);
    };

    const handleCloseUserMenu = () => {
        setAnchorElUser(null);
    };

    const handleSettingClick = (setting: string) => {
        if (setting === 'Logout') {
            logout();
        }else if(setting === 'Profile'){
            navigate(`/user/${user.id}`);
        }else if(setting ==='Admin'){
            navigate('/admin');
        }else if(setting ==='Dashboard'){
            navigate('/dashboard');
        }else if(setting === 'My Teams'){
            navigate('/myteams');
        }else if(setting === 'Account')
            navigate('/account');

        handleCloseUserMenu();
    };

    return (
        <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
            <Container maxWidth={false} sx={{ bgcolor: 'primary.main' }}>
                <Toolbar disableGutters>
                    <GamepadIcon sx={{ display: { xs: 'none', md: 'flex' }, mr: 1 }} />
                    <Typography
                        variant="h6"
                        noWrap
                        component={RouterLink}
                        to="/"
                        sx={{
                            mr: 2,
                            display: { xs: 'none', md: 'flex' },
                            fontFamily: 'Montserrat',
                            fontWeight: 700,
                            fontSize: "1.5rem",
                            letterSpacing: '1',
                            color: 'inherit',
                            textDecoration: 'none'
                        }}
                    >
                        Scrim Master
                    </Typography>

                    <Box sx={{ flexGrow: 1, display: { xs: 'flex', md: 'none' } }}>
                        <IconButton size="large" aria-label="open navigation menu" onClick={handleOpenNavMenu} color="inherit">
                            <MenuIcon />
                        </IconButton>
                        <Menu
                            id="menu-appbar"
                            anchorEl={anchorElNav}
                            anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
                            keepMounted
                            transformOrigin={{ vertical: 'top', horizontal: 'left' }}
                            open={Boolean(anchorElNav)}
                            onClose={handleCloseNavMenu}
                            sx={{ display: { xs: 'block', md: 'none' } }}
                        >
                            {pages.map((page) => (
                                <MenuItem key={page} onClick={handleCloseNavMenu}>
                                    <Button
                                        key={page}
                                        component={RouterLink}
                                        to={`/${page.toLowerCase()}`}
                                        onClick={handleCloseNavMenu}
                                        sx={{ my: 2, color: 'black', display: 'block', fontWeight: 700,fontFamily: 'Montserrat', ":hover" : {} , textAlign: 'center'}}
                                    >
                                        {page}
                                    </Button>
                                </MenuItem>
                            ))}

                            {!isAuthenticated &&
                                [
                                    <MenuItem
                                        key="login"
                                        component={RouterLink}
                                        to="/login"
                                        onClick={handleCloseNavMenu}
                                    >
                                        <Typography textAlign="center">Login</Typography>
                                    </MenuItem>,
                                    <MenuItem
                                        key="register"
                                        component={RouterLink}
                                        to="/register"
                                        onClick={handleCloseNavMenu}
                                    >
                                        <Typography textAlign="center">Register</Typography>
                                    </MenuItem>
                                ]




                            }

                        </Menu>
                    </Box>

                    <AdbIcon sx={{ display: { xs: 'flex', md: 'none' }, mr: 1 }} />
                    <Typography
                        variant="h5"
                        noWrap
                        component={RouterLink}
                        to="/"
                        sx={{
                            mr: 2,
                            display: { xs: 'flex', md: 'none' },
                            flexGrow: 1,
                            fontFamily: 'Montserrat',
                            fontWeight: 700,
                            letterSpacing: '.2rem',
                            color: 'inherit',
                            textDecoration: 'none'
                        }}
                    >
                        ScrimMaster
                    </Typography>

                    <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
                        {pages.map((page) => (
                            <Button
                                key={page}
                                component={RouterLink}
                                to={`/${page.toLowerCase()}`}
                                onClick={handleCloseNavMenu}
                                sx={{ my: 2, color: 'black', display: 'block', fontWeight: 700,fontFamily: 'Montserrat', ":hover" : {} }}
                            >
                                {page}
                            </Button>
                        ))}
                    </Box>

                    <Box sx={{ flexGrow: 0 }}>
                        {isAuthenticated ? (
                            <>
                                <Tooltip title="Open settings">
                                    <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                                        <PersonIcon fontSize="large" sx={{ color: 'background.default' }} />
                                    </IconButton>
                                </Tooltip>
                                <Menu
                                    sx={{ mt: '45px' }}
                                    id="menu-appbar"
                                    anchorEl={anchorElUser}
                                    anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
                                    keepMounted
                                    transformOrigin={{ vertical: 'top', horizontal: 'right' }}
                                    open={Boolean(anchorElUser)}
                                    onClose={handleCloseUserMenu}
                                >
                                    {settings.map((setting) => (
                                        <MenuItem key={setting} onClick={() => handleSettingClick(setting)}>
                                            <Typography textAlign="center" sx={{fontFamily: 'Montserrat'}}>{setting}</Typography>
                                        </MenuItem>
                                    ))}

                                    {user !== null && user.role==='ROLE_ADMIN' && [
                                        <MenuItem key={"Admin"} onClick={() => handleSettingClick("Admin")}>
                                            <Typography textAlign="center" sx={{fontFamily: 'Montserrat'}}>Admin Panel</Typography>
                                        </MenuItem>
                                    ]}
                                </Menu>
                            </>
                        ) : (
                            <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
                                <Button component={RouterLink} variant="outlined" to="/login" sx={{ borderColor : "secondary.main" , borderWidth: 2, fontFamily: 'Montserrat', color: 'black', fontWeight: 700, mr: 1}}>
                                    Login
                                </Button>
                                <Button component={RouterLink} variant="contained" to="/register" sx={{ bgcolor: "secondary.main", fontFamily: 'Montserrat', color: 'white', fontWeight: 700, }}>
                                    Register
                                </Button>
                            </Box>
                        )}
                    </Box>
                </Toolbar>
            </Container>
        </AppBar>
    );
};





export default ResponsiveAppBar;


