import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { CookieService } from 'ngx-cookie-service';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, RouterOutlet } from '@angular/router';
import { GameService } from '../../services/game.service';
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [NavbarComponent,CommonModule,RouterOutlet,HttpClientModule,ReactiveFormsModule],
  providers: [GameService],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  username = '';
  matchHistory: matchHistoryDTO[] = [];
  user: UserStatsDTO[] = [];
  isLoading=false;
  currentPage = 1;
  itemsPerPage = 5;
  totalItems = 0;
  constructor(private cookieService: CookieService,private gameService: GameService,private router:Router) { }
  redirectToGame(resultGameId: string) {
    this.router.navigate(['/game'], { queryParams: { gameId: resultGameId } });
  }
  async getGameHistory(){
    this.isLoading=true;
    this.gameService.getGames(this.username).subscribe((data: any[]) => {
      this.matchHistory = data;
      for (let i = 0; i < this.matchHistory.length; i++) {
        const match = this.matchHistory[i];
        const userStats = data[i].userStats[this.username];
        match.result = userStats.win ? "Win" : "Loss";
        if (match.result === "Loss") {
          [match.attackerLobbyName, match.defenderLobbyName] = [match.defenderLobbyName, match.attackerLobbyName];
          [match.attackerCaptain, match.defenderCaptain] = [match.defenderCaptain, match.attackerCaptain];
          match.score = match.score.split('-').reverse().join('-');
        }
      }
      this.totalItems = data.length;
      this.matchHistory = data.slice((this.currentPage - 1) * this.itemsPerPage, this.currentPage * this.itemsPerPage);
      this.isLoading = false;
    });
  }
  onPageChange(pageNumber: number) {
    this.currentPage = pageNumber;
    this.getGameHistory();
  }
  ngOnInit() {
    this.username=JSON.parse(this.cookieService.get('token')).username;
    this.getGameHistory();
  }
}
interface matchHistoryDTO {
  gameId:string;
  attackerLobbyName: string;
  attackerCaptain: string;
  defenderLobbyName: string;
  defenderCaptain: string;
  result: string;
  date: string;
  score: string;
}
interface UserStatsDTO {
  kills: number;
  deaths: number;
  hits: number;
  headshots: number;
  win: boolean;
}