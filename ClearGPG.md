#export GPG_TTY=$(tty)
#[INFO] --- maven-gpg-plugin:1.6:sign (sign-artifacts) @ json-schema-validator ---
#gpg: 抱歉，要求了无终端模式——无法获取输入
#出现这个错误是因为：GPG 不提示输入密码，即便执行 ：export GPG_TTY=$(tty)  也不生效，
#原因是： gpg-agent 挂了
#执行以下命令
```shell
  killall gpg-agent
```


#然后再重试：
```shell

echo "test" | gpg --clearsign

#若不行请使用以下命令重试：

export GPG_TTY=$(tty)
export GPG_PINENTRY_MODE=loopback
echo "test" | gpg --clearsign -
```

deploy
```shell

sh ./deploy.sh
```


