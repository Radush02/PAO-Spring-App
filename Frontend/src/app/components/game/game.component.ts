import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GameService } from '../../services/game.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [CommonModule],
  providers: [GameService],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css'
})
export class GameComponent {

  gameId='';
  isLoading=true;
  match!: matchHistoryDTO;
  usernames:string[]=[];
  winnerTeam:string='';
  loserTeam:string='';
  constructor(private route:Router, private params: ActivatedRoute,private gameService: GameService){
    this.params.queryParams.subscribe(params => {
      this.gameId = params['gameId'];
    });
  }
  redirectToHome() {
    this.route.navigate(['/dashboard']);
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