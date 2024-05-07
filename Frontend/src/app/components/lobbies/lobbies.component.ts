import { Component,OnInit } from '@angular/core';
import { RouterOutlet,Router} from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClientModule} from '@angular/common/http';
import { ReactiveFormsModule} from '@angular/forms';
import { LobbyService } from '../../services/lobby.service';
import { NavbarComponent } from '../navbar/navbar.component';
import { CookieService } from 'ngx-cookie-service';
import { FormsModule } from '@angular/forms';
import { GameService } from '../../services/game.service';
import { MatDialog } from '@angular/material/dialog';
import { PopupComponent } from '../popup/popup.component';

@Component({
  selector: 'app-lobbies',
  standalone: true,
  imports: [NavbarComponent,CommonModule,RouterOutlet,HttpClientModule,ReactiveFormsModule,FormsModule],
  providers: [LobbyService,GameService,PopupComponent],
  templateUrl: './lobbies.component.html',
  styleUrl: './lobbies.component.css'
})
export class LobbiesComponent implements OnInit {
  constructor(private dialog: MatDialog,private router:Router,private lobbyService: LobbyService,private cookieService:CookieService,private gameService: GameService ){

   }
   lobbies: LobbyDTO[] = [];
   leaders: string[] = [];
   isLoading = true;
   lobbyName = '';
   errorMessage: string = '';
   inLobby=false;
   username='';
   currentLobby = '';
   async getLobbies(){
    this.username = JSON.parse(this.cookieService.get('token')).username;
    this.lobbies = await this.lobbyService.getLobbies().toPromise();
    this.isLoading = false;
    const lobby = await this.lobbyService.inLobby(this.username).toPromise();
    this.currentLobby = lobby ? lobby.name : '';
    console.log(this.currentLobby);
  }
  async inThisLobby(lobbyName:string){
    return this.currentLobby === lobbyName;
  }
  async createLobby(){
    if(this.lobbyName == '') {
      this.errorMessage = 'Introdu un nume!';
      return;
    }
    try {
      await this.lobbyService.createLobby(this.username,this.lobbyName).toPromise();
      this.getLobbies();
      this.errorMessage = '';
    } catch (error:any) {
      this.errorMessage = error.error.split(': ')[1] || 'o7';
    }
  }
  async leaveLobby(lobbyLeader:string){
    try {
      await this.lobbyService.leaveLobby(lobbyLeader,this.username).toPromise();
      this.getLobbies();
    } catch (error:any) {
      this.errorMessage = error.error.split(': ')[1] || 'o7';
    }
  }
  async joinLobby(lobbyLeader:string){

    try {
      await this.lobbyService.joinLobby(lobbyLeader,this.username).toPromise();
      this.getLobbies();
    } catch (error:any) {
      this.errorMessage = error.error.split(': ')[1] || 'o7';
    }

  }
  async attackLobby(lobbyLeader:string){
    const dialogRef = this.dialog.open(PopupComponent);
  
    dialogRef.componentInstance.confirmAction.subscribe(async () => {
      this.gameService.attackLobby(lobbyLeader,this.username).subscribe(resultGameId => {
        this.router.navigate(['/game'],{queryParams:{gameId:resultGameId}});
      });
    });
  }
  async lobbyLeaders(){
    this.leaders = await this.lobbyService.allLeaders().toPromise();
    console.log(this.leaders);
  }
  ngOnInit() {
    this.lobbyLeaders();
    this.getLobbies();
  }
}
interface LobbyDTO{
  lobbyLeader:string;
  name:string;
  players:string[];
}