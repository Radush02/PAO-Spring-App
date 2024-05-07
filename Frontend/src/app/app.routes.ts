import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { RegisterComponent } from './components/register/register.component';
import { LobbiesComponent } from './components/lobbies/lobbies.component';
import { ChatsComponent } from './components/chats/chats.component';
import { ProfileComponent } from './profile/profile.component';
import { MessagesComponent } from './messages/messages.component';
import { GameComponent } from './components/game/game.component';
export const routes: Routes = [    
    {path: 'login', component:LoginComponent}
,{path: 'lobbies', component:LobbiesComponent},
{path: 'register', component:RegisterComponent},
{path: 'dashboard', component:DashboardComponent},
{path: 'chats', component:ChatsComponent},
{path:'profile',component:ProfileComponent},
{path:'messages',component:MessagesComponent},
{path: 'game', component:GameComponent},
];
