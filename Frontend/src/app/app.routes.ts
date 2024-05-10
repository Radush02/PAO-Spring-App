import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { RegisterComponent } from './components/register/register.component';
import { LobbiesComponent } from './components/lobbies/lobbies.component';
import { ChatsComponent } from './components/chats/chats.component';
import { ProfileComponent } from './components/profile/profile.component';
import { GameComponent } from './components/game/game.component';
import { AdminPanelComponent } from './components/admin-panel/admin-panel.component';
import { FriendsComponent } from './components/friends/friends.component';
export const routes: Routes = [    
    {path: 'login', component:LoginComponent}
,{path: 'lobbies', component:LobbiesComponent},
{path: 'register', component:RegisterComponent},
{path: 'dashboard', component:DashboardComponent},
{path: 'chats', component:ChatsComponent},
{path:'profile',component:ProfileComponent},
{path: 'game', component:GameComponent},
{path: 'friends', component:FriendsComponent},
{path: '', redirectTo: '/login', pathMatch: 'full'},
{path: 'adminPanel', component:AdminPanelComponent}
];
