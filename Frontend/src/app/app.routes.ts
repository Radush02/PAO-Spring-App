import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { RegisterComponent } from './components/register/register.component';
import { LobbiesComponent } from './components/lobbies/lobbies.component';
export const routes: Routes = [    
    {path: 'login', component:LoginComponent}
,{path: 'dashboard', component:DashboardComponent},
{path: 'register', component:RegisterComponent},
{path: 'lobbies', component:LobbiesComponent},
];
