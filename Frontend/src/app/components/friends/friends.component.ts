import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterOutlet } from '@angular/router';
import { FriendsService } from '../../services/friends.service';
import { NavbarComponent } from '../navbar/navbar.component';
import { CookieService } from 'ngx-cookie-service';
import { UserService } from '../../services/user.service';
import { MatDialog } from '@angular/material/dialog';
import { ErrorComponent } from '../../error/error.component';

@Component({
  selector: 'app-friends',
  standalone: true,
  imports: [CommonModule,RouterOutlet,HttpClientModule,ReactiveFormsModule,NavbarComponent],
  providers: [FriendsService,UserService],
  templateUrl: './friends.component.html',
  styleUrl: './friends.component.css'
})

export class FriendsComponent {
  user = JSON.parse(this.cookieService.get('token')).username;
  friends: any[] = [];
  requests: any[] = [];
  sent: any[] = [];
  loaded = false;
  addFriendForm:FormGroup;

  constructor(private dialog: MatDialog,private friendsService: FriendsService,private cookieService: CookieService,private fb: FormBuilder,private userService:UserService,private router:Router) {
    this.addFriendForm = this.fb.group({
      sender: [this.user],
      receiver: [null],
    });
  }

  showErrorDialog(message: string) {
    this.dialog.open(ErrorComponent, {
      data: { message: message },
    });
  }
  getRequests() {
    this.friendsService.getFriends(this.user).subscribe(
      (data) => {
        this.requests = data;
      },
      (error) => {
      this.showErrorDialog(error.error.split(': ')[1] || 'o7');
      }
    );
  }

  getFriends() {
    this.userService.getFriends(this.user).subscribe(
      (data) => {
        this.friends = data;
      },
      (error) => {
      this.showErrorDialog(error.error.split(': ')[1] || 'o7');
      }
    );
  }

  getSentRequests() {
    this.friendsService.sentRequests(this.user).subscribe(
      (data) => {
        this.sent = data;
      },
      (error) => {
      this.showErrorDialog(error.error.split(': ')[1] || 'o7');
      }
    );
  }

  addFriend() {
    if(this.addFriendForm.value.receiver==null){
      this.showErrorDialog('Introdu un nume.');
      return;
    }
    this.friendsService.add(this.addFriendForm.value).subscribe(
      (data) => {
        location.reload();
      },
      (error) => {
      this.showErrorDialog(error.error.split(': ')[1] || 'o7');
      }
    );
  }

  removeFriend(friend: string) {
    this.friendsService.delete(friend).subscribe(
      (data) => {
        location.reload();
      },
      (error) => {
      this.showErrorDialog(error.error.split(': ')[1] || 'o7');
      }
    );
  }

  accept(friend: string) {
    this.friendsService
      .response({ sender: friend, receiver: this.user, accepted: true })
      .subscribe(
        (data) => {
          location.reload();
        },
        (error) => {
        this.showErrorDialog(error.error.split(': ')[1] || 'o7');
        }
      );
  }

  decline(friend: string) {
    this.friendsService
      .response({ sender: friend, receiver: this.user, accepted: false })
      .subscribe(
        (data) => {
          location.reload();
        },
        (error) => {
        this.showErrorDialog(error.error.split(': ')[1] || 'o7');
        }
      );
  }

  cancel(friend: string) {
    this.friendsService
      .response({ sender: this.user, receiver: friend, accepted: false })
      .subscribe(
        (data) => {
          location.reload();
        },
        (error) => {
        this.showErrorDialog(error.error.split(': ')[1] || 'o7');
        }
      );
  }
  chat(to:string){
    this.router.navigate(['/chats'],{queryParams:{to:to}});
  }
  ngOnInit() {
    this.getFriends();
    this.getRequests();
    this.getSentRequests();
    this.loaded=true;
  }
}
interface RequestDTO{
  sender:string;
  receiver:string;
  accepted:boolean;
}