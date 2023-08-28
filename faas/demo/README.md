# AWS Lambda

## AWS CLI & SAM Install

설치는 모두 `MacOS` 기준으로 설치

### AWS CLI
```shell
brew install awscli
```

#### Configuration

```shell
aws configure
AWS Access Key ID [None] : [발급받은 IAM의 Access Key ID]
AWS Secret Access Key [None] : [발급받은 IAM의 Secret Access Key]
Default region name [None] : ap-northeast-2[서울 리전]
Default output format [None] : 
```

### SAM
```shell
brew tap aws/tap
brew install aws-sam-cli
```

## AWS Lambda 등록

### IAM Role 생성

```shell
aws iam create-role --role-name lambda-ex --assume-role-policy-document '{"Version": "2012-10-17","Statement": [{ "Effect": "Allow", "Principal": {"Service": "lambda.amazonaws.com"}, "Action": "sts:AssumeRole"}]}'
```

### Java Project 생성

-  Quarkus 사용

```shell
mvn archetype:generate \
       -DarchetypeGroupId=io.quarkus \
       -DarchetypeArtifactId=quarkus-amazon-lambda-archetype \
       -DarchetypeVersion=3.3.0
```

```shell
{
    "Role": {
        "Path": "/",
        "RoleName": "lambda-ex",
        "RoleId": "AROA53K4XS7WZ7NHBVAHC",
        "Arn": "arn:aws:iam::123456789012:role/lambda-ex",
        "CreateDate": "2023-08-28T09:05:40+00:00",
        "AssumeRolePolicyDocument": {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Principal": {
                        "Service": "lambda.amazonaws.com"
                    },
                    "Action": "sts:AssumeRole"
                }
            ]
        }
    }
}

```

#### RequestHandler 작성

```java
package co.example.demo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("greeting")
public class GreetingLambda implements RequestHandler<InputObject, OutputObject> {
    @Inject
    ProcessingService service;
    @Override
    public OutputObject handleRequest(InputObject inputObject, Context context) {
        OutputObject outputObject = service.process(inputObject);
        outputObject.setRequestId(context.getAwsRequestId());
        return outputObject;
    }
}
```

### Test 및 패키징

```shell
mvn test
mvn verify
```

`mvn verfiy` 를 하면 target 하위 디렉토리에 여러 파일 생성 됨

#### manage.sh

`AWS Lambda` 프로젝트 생성, 갱신, 삭제할 때 사용하며, 위에서 IAM 에 등록한 계정을 사용

#### function.zip 

바이너리가 담긴 배포파일

#### sam.jvm.yaml

자바 모드의 AWS SAM CLI 로컬 테스트 파일

#### sam.native.yaml

네이티브 모드의 AWS SAM CLI 로컬 테스트 파일

### 환경 변수 지정

위에서 IAM Role 을 생성할 때 출력된 결과중 arn 값을 환경변수로 지정한다.

```shell
LAMBDA_ROLE_ARN="arn:aws:iam::123456789012:role/lambda-ex"
```

### Local Test

```shell
sam local invoke --template target/sam.jvm.yaml --event payload.json
```

### AWS 배포

```shell
sh target/manage.sh create
sh target/manage.sh invoke
```

### Native Test

네이티브 모드로 실행하기 위해서는 GraalVM 이 필수이다

설치 하는방법은 따로 찾아보도록 하자

```shell
# linux
mvn -Pnative package

# other
mvn package -Pnative -Dquarkus.native.container-build=true \
-Dquarkus.native.container-runtime=docker
```

아주 기본적인 것들만 있으니 나중에 상세하기 lambda 공부해봐야겠다.
