import random
import time

def print_banner():
    print("*****************************************")
    print("*           숫자 맞히기 게임            *")
    print("*         난이도를 선택하세요!          *")
    print("*****************************************\n")

def select_difficulty():
    print("난이도를 선택하세요:")
    print("1. 쉬움 (1~10)")
    print("2. 보통 (1~50)")
    print("3. 어려움 (1~100)")
    while True:
        try:
            choice = int(input("난이도 (1, 2, 3 중 선택): "))
            if choice == 1:
                return 1, 10
            elif choice == 2:
                return 1, 50
            elif choice == 3:
                return 1, 100
            else:
                print("잘못된 선택입니다. 1, 2, 3 중 하나를 선택해주세요.")
        except ValueError:
            print("숫자를 입력해주세요.")

def get_player_name():
    while True:
        name = input("플레이어 이름을 입력하세요: ").strip()
        if name:
            return name
        print("이름은 비워둘 수 없습니다.")

def play_game(name, start, end):
    print(f"\n{name}님, 게임을 시작합니다!")
    print(f"숫자 범위는 {start}에서 {end}까지입니다.\n")
    
    target = random.randint(start, end)
    attempts = 0
    start_time = time.time()

    while True:
        try:
            guess = int(input(f"{start}~{end} 사이의 숫자를 입력하세요: "))
            attempts += 1
            if guess < start or guess > end:
                print(f"숫자는 {start}~{end} 사이여야 합니다!")
            elif guess < target:
                print("더 높은 숫자입니다!")
            elif guess > target:
                print("더 낮은 숫자입니다!")
            else:
                end_time = time.time()
                print(f"\n축하합니다, {name}님!")
                print(f"숫자 {target}을 맞히셨습니다.")
                print(f"시도 횟수: {attempts}")
                print(f"걸린 시간: {round(end_time - start_time, 2)}초")
                break
        except ValueError:
            print("숫자를 입력해주세요!")

def main():
    print_banner()
    name = get_player_name()
    start, end = select_difficulty()
    play_game(name, start, end)

if __name__ == "__main__":
    main()
