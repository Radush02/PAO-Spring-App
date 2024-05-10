import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GameService } from '../../services/game.service';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [CommonModule,NavbarComponent],
  providers: [GameService],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css'
})
export class GameComponent {
  role='';
  gameId='';
  isLoading=true;
  match!: matchHistoryDTO;
  usernames:string[]=[];
  winnerTeam:string='';
  loserTeam:string='';
  wonrounds=0;
  lostrounds=0;
  constructor(private route:Router, private params: ActivatedRoute,private gameService: GameService, private cookieService: CookieService) {
    this.params.queryParams.subscribe(params => {
      this.gameId = params['gameId'];
    });
  }
  downloadGame(){
    this.gameService.exportMultiplayerGame(this.gameId).subscribe(response=>{
      const contentDispositionHeader = response.headers.get('Content-Disposition');
      const filename = contentDispositionHeader
  ? contentDispositionHeader.split(';')[1].trim().split('=')[1].replace(/"/g, '')
  : 'contact_admin_issue.sb';
  const blob = new Blob([response.body], { type: 'application/json' });
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  window.URL.revokeObjectURL(url);
    })
  }
  async getGame(){
    this.match = await this.gameService.getGame(this.gameId).toPromise();
    this.winnerTeam = this.match.result === 'Win' ? this.match.attackerLobbyName : this.match.defenderLobbyName;
    this.loserTeam = this.match.result === 'Win' ? this.match.defenderLobbyName : this.match.attackerLobbyName;
   let userStats = this.match.userStats;
   this.usernames = Object.keys(userStats);
   console.log(this.usernames);
  }
  async ngOnInit(){
    await this.getGame();
    this.isLoading=false;
    this.role = JSON.parse(this.cookieService.get('token')).role;
  }
}
interface matchHistoryDTO {
  attackerLobbyName: string;
  attackerCaptain: string;
  defenderLobbyName: string;
  defenderCaptain: string;
  result: string;
  date: string;
  score: string;
  userStats: Record<string, UserStatsDTO>;
}
interface UserStatsDTO {
    kills: number;
    deaths: number;
    headshots: number;
    hits: number;
    win: boolean;
}