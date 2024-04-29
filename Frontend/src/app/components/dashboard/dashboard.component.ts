import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { CookieService } from 'ngx-cookie-service';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
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
  matchHistory=[];
  constructor(private cookieService: CookieService,private gameService: GameService) { }
  ngOnInit() {
    this.username=JSON.parse(this.cookieService.get('token')).username;
    this.gameService.getGames(this.username).subscribe((response: any) => {this.matchHistory=response;});
    console.log(this.matchHistory);
  }
}
interface matchHistoryDTO {
  teamName: string;
  gameDate: string;
  gameResult: string;
  //gameType: string;
  opponentName: string;
}